package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.WorkOrderModule;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidateJobPlanAssociationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if(!recordMap.containsKey(FacilioConstants.ContextNames.WORK_ORDER)){
            return false;
        }
        List<V3WorkOrderContext> workOrderContextArrayList = recordMap.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(CollectionUtils.isEmpty(workOrderContextArrayList)){
            return false;
        }
        for(V3WorkOrderContext workOrderContext : workOrderContextArrayList){
            if(workOrderContext.getPmV2() == null && workOrderContext.getJobPlan() != null){
                if(workOrderContext.getId() > 0){
                    V3WorkOrderContext updateWorkOrder = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER,workOrderContext.getId(),V3WorkOrderContext.class);
                    if(updateWorkOrder.getJobPlan() == null){
                        throw new IllegalArgumentException("Job plan should not be associated with Work Order during edit");
                    }
                    if(updateWorkOrder.getJobPlan().getId() != workOrderContext.getJobPlan().getId()){
                        throw new IllegalArgumentException("Job Plan cannot be changed for Work Order with ID #"+workOrderContext.getId());
                    }
                }
                else{
                    if(workOrderContext.getJobPlan() != null){
                        JobPlanContext jobPlanContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.JOB_PLAN,workOrderContext.getJobPlan().getId(),JobPlanContext.class);
                        if(jobPlanContext == null || jobPlanContext.getJobPlanCategoryEnum() == null || jobPlanContext.getJpStatusEnum() == null){
                            throw new IllegalArgumentException("Cannot associate the selected job plan with the work order. The job plan is either not categorized as \"General\" or it is not currently active");
                        }
                        if(jobPlanContext.getJpStatusEnum() != JobPlanContext.JPStatus.ACTIVE || jobPlanContext.getJobPlanCategoryEnum() != JobPlanContext.JPScopeAssignmentType.GENERAL){
                            throw new IllegalArgumentException("Only published job plans of the \"General\" category can be directly associated with a work order");
                        }
                    }
                }
            }
        }
        return false;
    }
}
