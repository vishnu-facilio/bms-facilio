package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderLabourContext;
import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderCostContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddorUpdateWoActualsCostforLabourCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<V3WorkOrderLabourContext> workOrderLabour = Constants.getRecordList((FacilioContext) context);


        long parent_Id = workOrderLabour.get(0).getParentId();

        if (parent_Id != -1) {
            int costType = 3;
            V3WorkorderCostContext.CostType cos = V3WorkorderCostContext.CostType.valueOf(costType);

            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR);
            String moduleName = FacilioConstants.ContextNames.WO_LABOUR;
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WO_LABOUR);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            FacilioField totalCost = fieldsMap.get("totalAmount");
            double cost = 0;

            SelectRecordsBuilder<V3WorkOrderLabourContext> recordsBuilderLabour = new SelectRecordsBuilder<V3WorkOrderLabourContext>()
                    .moduleName(moduleName)
                    .select(fields)
                    .beanClass(V3WorkOrderLabourContext.class)
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parent_Id), NumberOperators.EQUALS));

            List<V3WorkOrderLabourContext> recordListLabour = recordsBuilderLabour.get();
            for(V3WorkOrderLabourContext recordListLabours: recordListLabour){

                recordListLabours.getId();
                totalCost.getFieldId();

                FacilioModule recordModule = modBean.getModule(FacilioConstants.SystemLookup.CURRENCY_RECORD);
                List<FacilioField> selectFields = modBean.getAllFields(recordModule.getName());

                SelectRecordsBuilder<CurrencyRecord> select = new SelectRecordsBuilder<CurrencyRecord>()
                        .moduleName(recordModule.getName())
                        .select(selectFields)
                        .beanClass(CurrencyRecord.class)
                        .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(recordListLabours.getId()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", String.valueOf(totalCost.getFieldId()), NumberOperators.EQUALS));;

                List<CurrencyRecord> lists = select.get();
                for(CurrencyRecord list: lists){
                    cost += list.getBaseCurrencyValue();
                }
            }


            FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
            List<FacilioField> workorderCostsFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
            Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

            SelectRecordsBuilder<V3WorkorderCostContext> workorderCostSetlectBuilder = new SelectRecordsBuilder<V3WorkorderCostContext>()
                    .select(workorderCostsFields).table(workorderCostsModule.getTableName())
                    .moduleName(workorderCostsModule.getName()).beanClass(V3WorkorderCostContext.class)
                    .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
                            String.valueOf(parent_Id), PickListOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("costType"),
                            String.valueOf(cos.getValue()), EnumOperators.IS));

            List<V3WorkorderCostContext> workorderCosts = workorderCostSetlectBuilder.get();

            V3WorkorderCostContext workorderCost = new V3WorkorderCostContext();
            if (workorderCosts != null && !workorderCosts.isEmpty()) {
                workorderCost = workorderCosts.get(0);
                workorderCost.setCost(cost);
                workorderCost.setModifiedTime(System.currentTimeMillis());
                V3RecordAPI.updateRecord(workorderCost, workorderCostsModule, modBean.getAllFields(workorderCostsModule.getName()));
            } else {
                List<V3WorkorderCostContext> workOrderCostsList = new ArrayList<>();
                workorderCost.setCost(cost);
                V3WorkOrderContext wo = new V3WorkOrderContext();
                wo.setId(parent_Id);
                workorderCost.setParentId(wo);
                workorderCost.setCostType(3);
                workorderCost.setTtime(System.currentTimeMillis());
                workorderCost.setModifiedTime(System.currentTimeMillis());
                workOrderCostsList.add(workorderCost);
                V3RecordAPI.addRecord(false,workOrderCostsList,workorderCostsModule,modBean.getAllFields(workorderCostsModule.getName()));
            }
        }

        return false;
    }

}
