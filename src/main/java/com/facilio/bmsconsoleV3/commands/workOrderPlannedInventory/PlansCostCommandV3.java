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
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(workOrderId!=null && moduleName!=null){

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = new ArrayList<>();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));

            FacilioField totalCost = fieldMap.get("totalCost");
            fields.add(totalCost);

            if(moduleName == FacilioConstants.ContextNames.WO_PLANNED_ITEMS){
                SelectRecordsBuilder<WorkOrderPlannedItemsContext> recordsBuilder = new SelectRecordsBuilder<WorkOrderPlannedItemsContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedItemsContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

                recordsBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
                List<WorkOrderPlannedItemsContext> recordList = recordsBuilder.get();
                if(recordList.size()<1){
                    context.put(FacilioConstants.ContextNames.COST, 0);
                }else{
                    context.put(FacilioConstants.ContextNames.COST, recordList.get(0).getTotalCost());
                }
            }
            else if(moduleName == FacilioConstants.ContextNames.WO_PLANNED_TOOLS){
                SelectRecordsBuilder<WorkOrderPlannedToolsContext> recordsBuilder = new SelectRecordsBuilder<WorkOrderPlannedToolsContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedToolsContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

                recordsBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
                List<WorkOrderPlannedToolsContext> recordList = recordsBuilder.get();
                if(recordList.size()<1){
                    context.put(FacilioConstants.ContextNames.COST, 0);
                }else{
                    context.put(FacilioConstants.ContextNames.COST, recordList.get(0).getTotalCost());
                }
            }
            else if(moduleName == FacilioConstants.ContextNames.WO_PLANNED_SERVICES){
                SelectRecordsBuilder<WorkOrderPlannedServicesContext> recordsBuilder = new SelectRecordsBuilder<WorkOrderPlannedServicesContext>()
                        .moduleName(moduleName)
                        .beanClass(WorkOrderPlannedServicesContext.class)
                        .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workOrder", String.valueOf(workOrderId), NumberOperators.EQUALS));

                recordsBuilder.aggregate(BmsAggregateOperators.NumberAggregateOperator.SUM,  totalCost);
                List<WorkOrderPlannedServicesContext> recordList = recordsBuilder.get();
                if(recordList.size()<1){
                    context.put(FacilioConstants.ContextNames.COST, 0);
                }else{
                    context.put(FacilioConstants.ContextNames.COST, recordList.get(0).getTotalCost());
                }

            }
        }
        return false;
    }
}
