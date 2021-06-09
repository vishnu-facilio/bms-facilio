package com.facilio.bmsconsoleV3.context.inspection;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
@ToString(callSuper=true, includeFieldNames=true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public class InspectionTemplateContext extends QAndATemplateContext <InspectionResponseContext> {

    List<InspectionTriggerContext> triggers;
    public InspectionTemplateContext(Long id) {
        super(id);
    }
    
    private CreationType creationType;
    private PreventiveMaintenance.PMAssignmentType assignmentType;
    private BaseSpaceContext baseSpace;
    private AssetCategoryContext assetCategory;
    private SpaceCategoryContext spaceCategory;
    
    private ResourceContext resource;
    private VendorContext vendor;
    private TenantContext tenant;
    private InspectionCategoryContext category;
    private InspectionPriorityContext priority;
    private Group assignmentGroup;
    private User assignedTo;
    
    private Boolean status;
    
    public Integer getCreationType() { // Everything is wrapper in V3 for null handling
    	if(creationType != null) {
    		return creationType.getIndex();
    	}
    	return null;
    }
    
    public void setCreationType(Integer index) {
        creationType = index == null ? null : CreationType.valueOf(index);
    }
    
    public Integer getAssignmentType() { // Everything is wrapper in V3 for null handling
    	if(assignmentType != null) {
    		return assignmentType.getIndex();
    	}
    	return null;
    }
    
    public PreventiveMaintenance.PMAssignmentType getAssignmentTypeEnum() {
    	return assignmentType;
    }
    
    public void setAssignmentType(Integer index) {
        assignmentType = index == null ? null : PreventiveMaintenance.PMAssignmentType.valueOf(index);
    }

    @Override
    protected InspectionResponseContext newResponseObject() {
        return new InspectionResponseContext();
    }

    @Override
    protected void addDefaultPropsForResponse(InspectionResponseContext response) {

    	response.setSiteId(this.getSiteId());
        response.setVendor(this.getVendor());
        response.setTenant(this.getTenant());
        response.setCategory(this.getCategory());
        response.setPriority(this.getPriority());
        response.setAssignedTo(this.getAssignedTo());
        response.setAssignmentGroup(this.getAssignmentGroup());

        // Default props which will be overridden if called from Inspection scheduler
        response.setSourceType(InspectionResponseContext.SourceType.MANNUAL.getIndex());
        response.setCreatedTime(System.currentTimeMillis());
        response.setStatus(InspectionResponseContext.Status.OPEN.getIndex());
    }

    @AllArgsConstructor
    @Getter
    public static enum CreationType implements FacilioIntEnum {
		
		SINGLE("Single"), 
		MULTIPLE("Multiple"),
		;
    	
		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		private static final CreationType[] CREATION_TYPES = CreationType.values();
		public static CreationType valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}

	@Override
	protected List<InspectionResponseContext> newResponseObjects() throws Exception{
		// TODO Auto-generated method stub
		DateRange range = new DateRange(0l, DateTimeUtil.getCurrenTime());
		List<InspectionResponseContext> responses = new InspectionScheduler().getResponses(this, null, Collections.singletonList(range));
		return responses;
	}
    
}
