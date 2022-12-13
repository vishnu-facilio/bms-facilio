package com.facilio.readingrule.faultimpact;

import java.util.List;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants.ContextNames.SpaceCategory;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.context.WorkflowContext;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FaultImpactContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	String linkName;
	String description;
	PreventiveMaintenance.PMAssignmentType type;
	AssetCategoryContext assetCategory;
	SpaceCategory spaceCategory;
	Long workflowId;
	WorkflowContext workflow;
	
	List<FaultImpactNameSpaceFieldContext> fields;

	NameSpaceContext ns;
	
	public PreventiveMaintenance.PMAssignmentType getTypeEnum() {
		return type;
	}
	public int getType() {
		if(type != null) {
			return type.getVal();
		}
		return -1;
	}

	public void setType(Integer type) {
		this.type = PreventiveMaintenance.PMAssignmentType.valueOf(type);
	}
	
	
}
