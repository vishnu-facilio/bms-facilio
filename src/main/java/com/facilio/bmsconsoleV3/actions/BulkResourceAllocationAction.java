package com.facilio.bmsconsoleV3.actions;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.util.BulkResourceAllocationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.v3.V3Action;
import com.facilio.v3.util.V3Util;

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
    Long meterTypeId;
    
    List<Long> siteIds;
    List<Long> baseSpaceIds;
    
    Long qAndATemplateId;
    
    public String getModuleAndCrtieriaFromConfig() throws Exception{
    	
    	 if(getModuleName() != null && getqAndATemplateId() != null) {
    		 switch(getModuleName()) {
	    		 case FacilioConstants.Inspection.INSPECTION_TEMPLATE: {
	
	    			 InspectionTemplateContext record = (InspectionTemplateContext) V3Util.getRecord(getModuleName(), qAndATemplateId, null);
	    			 if(record != null && record.getTriggers() != null ) {
	    				 List<InspectionTriggerContext> manualTriggers = record.getTriggers().stream().filter(t -> t.getType() == InspectionTriggerContext.TriggerType.USER.getVal()).collect(Collectors.toList());
	    				 if(manualTriggers != null && !manualTriggers.isEmpty()) {
	    					 List<InspectionTriggerIncludeExcludeResourceContext> inclExcls = manualTriggers.get(0).getResInclExclList();
	    					 if(inclExcls != null) {
	    						 
	    						 List<Long> inclList = new ArrayList<Long>();
	    						 
	    						 for(InspectionTriggerIncludeExcludeResourceContext inclExcl : inclExcls) {
	    							 if(inclExcl.getIsInclude() && inclExcl.getResourceId() > 0) {
	    								 inclList.add(inclExcl.getResourceId());
	    							 }
	    						 }
	    						 if(!inclList.isEmpty()) {
	    							 JSONObject returnObject = new JSONObject();
	    							 Criteria criteria = new Criteria();
	    							 criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(inclList, ","), PickListOperators.IS));
	    							 
	    							 returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.RESOURCE);
	    							 returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
	    							 
	    							 setData(returnObject);
	    							 
	    							 return SUCCESS;
	    						 }
	    					 }
	    				 }
	    			 }
	    		 }
	    		 break;
		    	 case FacilioConstants.Induction.INDUCTION_TEMPLATE: {
		    		 InductionTemplateContext record = (InductionTemplateContext) V3Util.getRecord(getModuleName(), qAndATemplateId, null);
					 if(record != null) {
						 List<InductionTriggerContext> manualTriggers = record.getTriggers().stream().filter(t -> t.getType() == InductionTriggerContext.TriggerType.USER.getVal()).collect(Collectors.toList());
						 if(manualTriggers != null && !manualTriggers.isEmpty()) {
							 List<InductionTriggerIncludeExcludeResourceContext> inclExcls = manualTriggers.get(0).getResInclExclList();
							 if(inclExcls != null) {
								 
								 List<Long> inclList = new ArrayList<Long>();
								 
								 for(InductionTriggerIncludeExcludeResourceContext inclExcl : inclExcls) {
									 if(inclExcl.getIsInclude() && inclExcl.getResourceId() > 0) {
										 inclList.add(inclExcl.getResourceId());
									 }
								 }
								 if(!inclList.isEmpty()) {
									 JSONObject returnObject = new JSONObject();
									 Criteria criteria = new Criteria();
									 criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(inclList, ","), PickListOperators.IS));
									 
									 returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.RESOURCE);
									 returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
									 
									 setData(returnObject);
									 
									 return SUCCESS;
								 }
							 }
						 }
					 }
				 }
		    	 break;
    		 }
    			 
    	 }
    	
    	 if(baseSpaceIds != null || siteIds != null) {
    		 
    		 JSONObject res = BulkResourceAllocationUtil.getMultipleResourceCriteriaFromConfig(PreventiveMaintenance.PMAssignmentType.valueOf(assignmentTypeId), siteIds, baseSpaceIds, spaceCategoryId, assetCategoryId,meterTypeId);
        	 
        	 setData(res);
    	 }
    	 else {
    		 
    		 JSONObject res = BulkResourceAllocationUtil.getMultipleResourceCriteriaFromConfig(PreventiveMaintenance.PMAssignmentType.valueOf(assignmentTypeId), siteId, baseSpaceId, spaceCategoryId, assetCategoryId,meterTypeId);
        	 
        	 setData(res);
    	 }
    	
    	return SUCCESS;
    }

	public Long getqAndATemplateId() {
		return qAndATemplateId;
	}

	public void setqAndATemplateId(Long qAndATemplateId) {
		this.qAndATemplateId = qAndATemplateId;
	}
}
