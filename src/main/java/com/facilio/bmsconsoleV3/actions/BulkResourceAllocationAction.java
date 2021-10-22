package com.facilio.bmsconsoleV3.actions;


import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.util.BulkResourceAllocationUtil;
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
    
    List<Long> siteIds;
    List<Long> buildingIds;
    
    public String getModuleAndCrtieriaFromConfig(){
    	
    	 if(buildingIds != null || siteIds != null) {
    		 
    		 JSONObject res = BulkResourceAllocationUtil.getMultipleResourceCriteriaFromConfig(PreventiveMaintenance.PMAssignmentType.valueOf(assignmentTypeId), siteIds, buildingIds, spaceCategoryId, assetCategoryId);
        	 
        	 setData(res);
    	 }
    	 else {
    		 
    		 JSONObject res = BulkResourceAllocationUtil.getMultipleResourceCriteriaFromConfig(PreventiveMaintenance.PMAssignmentType.valueOf(assignmentTypeId), siteId, baseSpaceId, spaceCategoryId, assetCategoryId);
        	 
        	 setData(res);
    	 }
    	
    	return SUCCESS;
    }
}
