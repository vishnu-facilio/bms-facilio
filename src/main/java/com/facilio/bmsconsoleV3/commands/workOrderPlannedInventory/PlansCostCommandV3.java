package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

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

        }
        return false;
    }
}
