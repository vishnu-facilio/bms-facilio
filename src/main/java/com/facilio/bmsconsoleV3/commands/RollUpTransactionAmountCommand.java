package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RollUpTransactionAmountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TRANSACTION);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<V3TransactionContext> recordList = Constants.getRecordListFromMap(recordMap, module.getName());
        V3TransactionContext record= recordList.get(0);

        FacilioField timeFieldCloned = fieldMap.get("transactionDate").clone();
        FacilioField groupingTimeField = BmsAggregateOperators.DateAggregateOperator.MONTH.getSelectField(timeFieldCloned);

        List<FacilioField> selectFields = new ArrayList<>();

        selectFields.add(fieldMap.get("account"));
        selectFields.add(fieldMap.get("transactionResource"));

        selectFields.add(FieldFactory.getField("creditAmount", "sum(case WHEN TRANSACTION_TYPE = 1 THEN TRANSACTION_AMOUNT END )",
                FieldType.DECIMAL));
        selectFields.add(FieldFactory.getField("debitAmount", "sum(case WHEN TRANSACTION_TYPE = 2 THEN TRANSACTION_AMOUNT END )",
                FieldType.DECIMAL));

        SelectRecordsBuilder<V3TransactionContext> builder = new SelectRecordsBuilder<V3TransactionContext>()
                .module(module)
                .beanClass(V3TransactionContext.class)
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("account"), String.valueOf(record.getAccount().getId()), PickListOperators.IS))
               // .andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionResource"), "1", CommonOperators.IS_NOT_EMPTY))
                .groupBy(groupingTimeField.getCompleteColumnName()+ "," +fieldMap.get("account").getCompleteColumnName()+"," + fieldMap.get("transactionResource").getCompleteColumnName())
                ;

        Set<Long> resource_ids = (Set<Long>) Constants.getBodyParams(context).get("Resource_ids");
        Criteria criteria = new Criteria();
        if (resource_ids.contains(-1l)) {
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("transactionResource"), CommonOperators.IS_EMPTY));
        }
        Set<Long> filteredResource_id = resource_ids.stream().filter( id -> !(id.equals(-1l))).collect(Collectors.toSet());
        if (!filteredResource_id.isEmpty()) {
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("transactionResource"), filteredResource_id, PickListOperators.IS));
        }
        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }

        builder.aggregate(BmsAggregateOperators.NumberAggregateOperator.MIN, timeFieldCloned);
        List<Map<String, Object>> mapList = builder.getAsProps();

        Set<Long> availableResourceIds = new HashSet<>();
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

                Map<String, Object> resourceMap = (Map<String, Object>) map.get("transactionResource");
                if (resourceMap == null) {
                    availableResourceIds.add(-1l);
                } else {
                    availableResourceIds.add((Long) resourceMap.get("id"));
                }

                String rollUpModName = FacilioConstants.TransactionRule.TransactionRollUpModuleName;
                Long minMonthStartDate = (Long)map.get("transactionDate");
                String rollUpFieldName = FacilioConstants.TransactionRule.TransactionRollUpFieldName;
                Map<String, Object> accountMap = (Map<String, Object>) map.get("account");
                final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
                Double actualAmount = Double.valueOf(df.format(amount));
                Long monthStartDate = DateTimeUtil.getMonthStartTimeOf(minMonthStartDate, false);
                rollUpData(actualAmount, accountMap, resourceMap, rollUpModName, rollUpFieldName, monthStartDate);

            }

        }

        if (!availableResourceIds.containsAll(resource_ids)) {
            resource_ids.removeAll(availableResourceIds);
            for (long resourceId : resource_ids) {
                final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);

                Long minMonthStartDate = record.getTransactionDate();
                Long monthStartDate = DateTimeUtil.getMonthStartTimeOf(minMonthStartDate, false);

                Map<String, Object> accountMap = new HashMap<>();
                accountMap.put("id", record.getAccount().getId());

                Map<String, Object> resourceMap = new HashMap<>();
                if (record.getTransactionResource() != null) {
                    resourceMap.put("id", resourceId);
                }

                String rollUpModName = FacilioConstants.TransactionRule.TransactionRollUpModuleName;
                String rollUpFieldName = FacilioConstants.TransactionRule.TransactionRollUpFieldName;

                rollUpData(Double.valueOf(df.format(0)), accountMap, resourceMap, rollUpModName, rollUpFieldName, monthStartDate);
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
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("startDate"), String.valueOf(startDate), DateOperators.IS));

        if (MapUtils.isNotEmpty(resourceMap) && resourceMap.containsKey("id")) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(resourceMap.get("id")), PickListOperators.IS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), "1", CommonOperators.IS_EMPTY));
        }

        List<Map<String, Object>> mapList = builder.getAsProps();

        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                ids.add((Long) map.get("id"));
                map.put(rollUpFieldName, amount);
            }
        }
        if (CollectionUtils.isNotEmpty(ids) && CollectionUtils.isNotEmpty(mapList)) {
            FacilioContext summaryContext = V3Util.getSummary(rollUpModName, ids);
            List<ModuleBaseWithCustomFields> oldRecord = Constants.getRecordListFromContext(summaryContext, rollUpModName);
            V3Util.processAndUpdateBulkRecords(module, oldRecord, mapList, null, null, null,
                    null, null, null, null, false);
        }
    }
}
