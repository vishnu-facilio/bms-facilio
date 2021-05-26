package com.facilio.bmsconsoleV3.actions;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BulkResourceAllocationAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer assignmentTypeId;
    Long siteId;
    Long baseSpaceId;
    Long spaceCategoryId;
    Long assetCategoryId;
    
    public String getModuleAndCrtieriaFromConfig(){
		
    	 JSONObject res = PreventiveMaintenanceAPI.getMultipleResourceCriteriaFromConfig(PreventiveMaintenance.PMAssignmentType.valueOf(assignmentTypeId), siteId, baseSpaceId, spaceCategoryId, assetCategoryId);
    	 
    	 setData(res);
    	
    	return SUCCESS;
    }
}
