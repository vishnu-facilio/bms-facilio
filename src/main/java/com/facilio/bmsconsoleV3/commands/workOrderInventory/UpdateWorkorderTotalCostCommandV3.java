package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderCostContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateWorkorderTotalCostCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
        Boolean isWorkOrderCostChain = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_WORKORDER_COST_CHAIN, false);

        if (parentIds != null && !parentIds.isEmpty()) {
            if(getConditionForCostUpdate(parentIds, isWorkOrderCostChain)){
                Long parentId = parentIds.get(0);
                FacilioModule workorderCostsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST);
                List<FacilioField> workorderCostsFields = modBean
                        .getAllFields(FacilioConstants.ContextNames.WORKORDER_COST);
                Map<String, FacilioField> workorderCostsFieldMap = FieldFactory.getAsMap(workorderCostsFields);

                SelectRecordsBuilder<V3WorkorderCostContext> workorderCostsSetlectBuilder = new SelectRecordsBuilder<V3WorkorderCostContext>()
                        .select(workorderCostsFields).table(workorderCostsModule.getTableName())
                        .moduleName(workorderCostsModule.getName()).beanClass(V3WorkorderCostContext.class)
                        .andCondition(CriteriaAPI.getCondition(workorderCostsFieldMap.get("parentId"),
                                String.valueOf(parentId), PickListOperators.IS));

                List<V3WorkorderCostContext> workorderCostsList = workorderCostsSetlectBuilder.get();
                double totalcost = 0;
                for (V3WorkorderCostContext wo : workorderCostsList) {
                    totalcost += wo.getCost();
                }

                V3WorkOrderContext workorder = (V3WorkOrderContext) V3Util.getRecord("workorder", parentId,null);
                workorder.setTotalCost(totalcost);

                context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, null);
                context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
                context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, parentIds);
                context.put(FacilioConstants.ContextNames.RECORD_LIST, java.util.Collections.singletonList(workorder));
                context.put(FacilioConstants.ContextNames.WO_TOTAL_COST, totalcost);

                V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.WORK_ORDER, parentId, FieldUtil.getAsJSON(workorder), null, null, null, null, null,null, null, null);

            }
            else{
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "There are more than one parent Id");
            }
        }
        return false;
    }

    private boolean getConditionForCostUpdate(List<Long> parentIds, Boolean isWorkOrderCostChain){
        if(isWorkOrderCostChain){
            return parentIds.size() > 0;
        }else{
            return parentIds.size()==1;
        }
    }
}
