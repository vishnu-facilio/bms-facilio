package com.facilio.bmsconsoleV3.context.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
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
public class SurveyTemplateContext extends QAndATemplateContext <SurveyResponseContext> {

    List<SurveyTriggerContext> triggers;
    public SurveyTemplateContext(Long id) {
        super(id);
    }
    
    private List<SiteContext> sites;
    private List<BuildingContext> buildings;
    private CreationType creationType;
    private PreventiveMaintenance.PMAssignmentType assignmentType;
    private AssetCategoryContext assetCategory;
    private SpaceCategoryContext spaceCategory;
    
    private ResourceContext resource;
    
    private Boolean status;
    
    private PeopleContext assignedTo;


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
    protected SurveyResponseContext newResponseObject() {

		SurveyResponseContext surveyResponse = new SurveyResponseContext();
        
    	surveyResponse.setData(this.getData());
        
        return surveyResponse;
    }
    
    @Override
	protected List<SurveyResponseContext> newResponseObjects(List<ResourceContext> resources) throws Exception {
		// TODO Auto-generated method stub
    	
    	List<SurveyResponseContext> responses = null;
    	if(CollectionUtils.isNotEmpty(resources) && this.creationType == CreationType.MULTIPLE) {
			responses = new ArrayList<SurveyResponseContext>();
			for(ResourceContext resource : resources) {
				resource = ResourceAPI.getResource(resource.getId());
				SurveyResponseContext response = newResponseObject();
				response.setResource(resource);
				response.setSiteId(resource.getSiteId());
				responses.add(response);
			}
		}
		else {
			DateRange range = new DateRange(0l, DateTimeUtil.getCurrenTime());
			responses = new SurveyScheduler().getResponses(this, null, Collections.singletonList(range));
		}
		return responses;
    	
	}

    @Override
    protected void addDefaultPropsForResponse(SurveyResponseContext response) {
       
    	if(response.getSiteId() <= 0) {
    		response.setSiteId(this.getSiteId());
    	}

        // Default props which will be overridden if called from Inspection scheduler
        response.setSourceType(InductionResponseContext.SourceType.MANNUAL.getIndex());
        response.setCreatedTime(System.currentTimeMillis());
        response.setScheduledWorkStart(response.getCreatedTime());
        response.setStatus(InductionResponseContext.Status.OPEN.getIndex());
        response.setAssignedTo(this.getAssignedTo());
    }

    @AllArgsConstructor
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
	public SurveyResponseContext constructResponse(){

		return (SurveyResponseContext) super.constructResponse();
	}
}
