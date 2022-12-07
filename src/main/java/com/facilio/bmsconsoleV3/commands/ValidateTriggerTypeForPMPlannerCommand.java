package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;

public class ValidateTriggerTypeForPMPlannerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	List<PMPlanner> plannerList = (List<PMPlanner>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.PM_V2.PM_V2_PLANNER));
    	
    	if(CollectionUtils.isNotEmpty(plannerList)) {
    		for(PMPlanner planner : plannerList) {
    			
    			if(planner.getTrigger() != null && planner.getTrigger().getFrequencyEnum() != null) {
    				
    				List<PMPlanner> existingPlanners = PlannedMaintenanceAPI.getPmPlanners(Collections.singletonList(planner.getPmId()));
        			
        			if(CollectionUtils.isNotEmpty(existingPlanners)) {
        				
        				for(PMPlanner existingPlanner : existingPlanners) {
        					
        					existingPlanner = (PMPlanner) V3Util.getRecord(FacilioConstants.PM_V2.PM_V2_PLANNER, existingPlanner.getId(), null);
        					
            				if(planner.getId() != existingPlanner.getId() && existingPlanner.getTrigger() != null && existingPlanner.getTrigger().getFrequencyEnum() != null) {
            					if(existingPlanner.getTrigger().getFrequencyEnum() == planner.getTrigger().getFrequencyEnum()) {
            						
            						V3Util.throwRestException(Boolean.TRUE, ErrorCode.VALIDATION_ERROR, existingPlanner.getName()+" with same trigger frequency already exist, Please select another trigger frequency to continue");
            					}
            				}
            			}
        			}
    			}
        	}
    	}
		return CONTINUE_PROCESSING;
	}

}
