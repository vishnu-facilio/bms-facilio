package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

public class RollUpTransactionAmountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TRANSACTION);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        FacilioField timeFieldCloned = fieldMap.get("transactionDate").clone();
        FacilioField groupingTimeField = BmsAggregateOperators.DateAggregateOperator.MONTH.getSelectField(timeFieldCloned);

        List<FacilioField> selectFields = new ArrayList<>();

        selectFields.add(fieldMap.get("account"));
        selectFields.add(fieldMap.get("transactionResource"));
        selectFields.add(fieldMap.get("transactionRollUpModuleName"));
        selectFields.add(fieldMap.get("transactionRollUpFieldName"));

        selectFields.add(FieldFactory.getField("creditAmount", "sum(case WHEN TRANSACTION_TYPE = 1 THEN TRANSACTION_AMOUNT END )",
                FieldType.DECIMAL));
        selectFields.add(FieldFactory.getField("debitAmount", "sum(case WHEN TRANSACTION_TYPE = 2 THEN TRANSACTION_AMOUNT END )",
                FieldType.DECIMAL));

        SelectRecordsBuilder<V3TransactionContext> builder = new SelectRecordsBuilder<V3TransactionContext>()
                .module(module)
                .beanClass(V3TransactionContext.class)
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("account"), "1", CommonOperators.IS_NOT_EMPTY))
               // .andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionResource"), "1", CommonOperators.IS_NOT_EMPTY))
                .groupBy(groupingTimeField.getCompleteColumnName()+ "," +fieldMap.get("account").getCompleteColumnName()+"," + fieldMap.get("transactionResource").getCompleteColumnName() +"," +fieldMap.get("transactionRollUpFieldName").getCompleteColumnName() +"," + fieldMap.get("transactionRollUpModuleName").getCompleteColumnName())
                ;

        builder.aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, timeFieldCloned);
        List<Map<String, Object>> mapList = builder.getAsProps();

        if(CollectionUtils.isNotEmpty(mapList)) {
            for(Map<String, Object> map : mapList){
                Double creditAmount = (Double) map.get("creditAmount");
                Double debitAmount = (Double) map.get("debitAmount");
                if(creditAmount == null) {
                    creditAmount = 0d;
                }
                if(debitAmount == null){
                    debitAmount = 0d;
                }
                Double amount = creditAmount - debitAmount;

                Map<String, Object> resourceMap = (Map<String, Object>)map.get("transactionResource");
                String rollUpModName = (String)map.get("transactionRollUpModuleName");
                Long minMonthStartDate = (Long)map.get("transactionDate");
                String rollUpFieldName = (String)map.get("transactionRollUpFieldName");
                Map<String, Object> accountMap = (Map<String, Object>) map.get("account");
                final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
                Double actualAmount = Double.valueOf(df.format(amount));
                Long monthStartDate = DateTimeUtil.getMonthStartTimeOf(minMonthStartDate, false);
                if(StringUtils.isNotEmpty(rollUpModName) && StringUtils.isNotEmpty(rollUpFieldName)) {
                    rollUpData(actualAmount, accountMap, resourceMap, rollUpModName, rollUpFieldName, monthStartDate);
                }

            }
        }
        return false;
    }

    private void rollUpData(Double amount, Map<String, Object> accountMap, Map<String, Object> resourceMap, String rollUpModName, String rollUpFieldName, long startDate) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(rollUpModName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));


        SelectRecordsBuilder<V3Context> builder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .beanClass(V3Context.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("account"), String.valueOf(accountMap.get("id")), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("startDate"), String.valueOf(startDate), DateOperators.IS))
                ;

        if(MapUtils.isNotEmpty(resourceMap) && resourceMap.containsKey("id")){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(resourceMap.get("id")), PickListOperators.IS));
        }

        List<Map<String, Object>> mapList = builder.getAsProps();

        if(CollectionUtils.isNotEmpty(mapList)){
            Collection<JSONObject> jsonList = new ArrayList<>();
            for(Map<String, Object> map : mapList){
                map.put(rollUpFieldName, amount);
                JSONObject json = new JSONObject();
                json.putAll(map);
                jsonList.add(json);
            }

            FacilioChain patchChain = ChainUtil.getBulkPatchChain(rollUpModName);

            FacilioContext context = patchChain.getContext();
            V3Config v3Config = ChainUtil.getV3Config(rollUpModName);
            Class beanClass = ChainUtil.getBeanClass(v3Config, module);

            Constants.setModuleName(context, rollUpModName);
            Constants.setBulkRawInput(context, (Collection)jsonList);
            context.put(Constants.BEAN_CLASS, beanClass);
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
            context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            patchChain.execute();

        }


    }
}
