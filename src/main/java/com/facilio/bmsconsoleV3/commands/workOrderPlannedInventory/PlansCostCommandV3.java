package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderLabourPlanContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import java.util.LinkedHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlansCostCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(workOrderId!=null){
            // PLANNED ITEMS COST
            String moduleName = FacilioConstants.ContextNames.WO_PLANNED_ITEMS;
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            FacilioField totalCost = fieldMap.get("totalCost");


            SelectRecordsBuilder<WorkOrderPlannedItemsContext> recordsBuilder = new SelectRecordsBuilder<WorkOrderPlannedItemsContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedItemsContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

            recordsBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
            List<WorkOrderPlannedItemsContext> recordList = recordsBuilder.get();
            if(recordList.size()<1){
                    context.put(FacilioConstants.ContextNames.PLANNED_ITEMS_COST, 0);
            }else{
                    context.put(FacilioConstants.ContextNames.PLANNED_ITEMS_COST, recordList.get(0).getTotalCost());
            }

            // PLANNED TOOLS COST
            moduleName = FacilioConstants.ContextNames.WO_PLANNED_TOOLS;
            fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            totalCost = fieldMap.get("totalCost");

            SelectRecordsBuilder<WorkOrderPlannedToolsContext> recordsBuilderTools = new SelectRecordsBuilder<WorkOrderPlannedToolsContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedToolsContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

            recordsBuilderTools.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
            List<WorkOrderPlannedToolsContext> recordListTools = recordsBuilderTools.get();
            if(recordListTools.size()<1){
                    context.put(FacilioConstants.ContextNames.PLANNED_TOOLS_COST, 0);
            }else{
                    context.put(FacilioConstants.ContextNames.PLANNED_TOOLS_COST, recordListTools.get(0).getTotalCost());
            }

            // PLANNED SERVICES COST
            moduleName = FacilioConstants.ContextNames.WO_PLANNED_SERVICES;
            fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            totalCost = fieldMap.get("totalCost");

            SelectRecordsBuilder<WorkOrderPlannedServicesContext> recordsBuilderServices = new SelectRecordsBuilder<WorkOrderPlannedServicesContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedServicesContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

            recordsBuilderServices.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
            List<WorkOrderPlannedServicesContext> recordListServices = recordsBuilderServices.get();
            if(recordListServices.size()<1){
                    context.put(FacilioConstants.ContextNames.PLANNED_SERVICES_COST, 0);
            }else{
                    context.put(FacilioConstants.ContextNames.PLANNED_SERVICES_COST, recordListServices.get(0).getTotalCost());
            }

            //PLANNED LABOUR COST
            moduleName = FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN;
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
            fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            totalCost = fieldMap.get("totalPrice");
            double cost = 0;

            SelectRecordsBuilder<V3WorkOrderLabourPlanContext> recordsBuilderLabour = new SelectRecordsBuilder<V3WorkOrderLabourPlanContext>()
                    .moduleName(moduleName)
                    .select(fields)
                    .beanClass(V3WorkOrderLabourPlanContext.class)
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(workOrderId), NumberOperators.EQUALS));

            List<V3WorkOrderLabourPlanContext> recordListLabour = recordsBuilderLabour.get();
            for(V3WorkOrderLabourPlanContext recordListLabours: recordListLabour){

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
            if(recordListLabour.size()<1){
                context.put(FacilioConstants.ContextNames.PLANNED_LABOUR_COST, 0);
            }else{
                context.put(FacilioConstants.ContextNames.PLANNED_LABOUR_COST, cost);
            }
        }
        return false;
    }
}
