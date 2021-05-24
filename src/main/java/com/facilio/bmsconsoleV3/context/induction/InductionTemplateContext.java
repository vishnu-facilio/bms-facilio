package com.facilio.bmsconsoleV3.context.induction;

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
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
public class InductionTemplateContext extends QAndATemplateContext <InductionResponseContext> {
    private SiteContext site;

    List<InductionTriggerContext> triggers;
    public InductionTemplateContext(Long id) {
        super(id);
    }
    
    private CreationType creationType;
    private PreventiveMaintenance.PMAssignmentType assignmentType;
    private BaseSpaceContext baseSpace;
    private AssetCategoryContext assetCategory;
    private SpaceCategoryContext spaceCategory;
    
    private ResourceContext resource;
    private Group assignmentGroup;
    private User assignedTo;
    
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
    protected InductionResponseContext newResponseObject() {
        return new InductionResponseContext();
    }

    @Override
    protected void addDefaultPropsForResponse(InductionResponseContext response) {
        response.setSite(this.getSite());
        response.setSiteId(this.getSiteId());
        response.setAssignedTo(this.getAssignedTo());
        response.setAssignmentGroup(this.getAssignmentGroup());

        // Default props which will be overridden if called from Inspection scheduler
        response.setSourceType(InductionResponseContext.SourceType.MANNUAL.getIndex());
        response.setCreatedTime(System.currentTimeMillis());
        response.setStatus(InductionResponseContext.Status.OPEN.getIndex());
    }

    public static enum CreationType implements FacilioIntEnum {
		
		SINGLE, 
		MULTIPLE,
		;
		public int getVal() {
			return ordinal() + 1;
		}
		private static final CreationType[] CREATION_TYPES = CreationType.values();
		public static CreationType valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
    
}

