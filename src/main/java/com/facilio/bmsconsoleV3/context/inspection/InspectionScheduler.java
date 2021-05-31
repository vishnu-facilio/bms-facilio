package com.facilio.bmsconsoleV3.context.inspection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.bmsconsole.context.ScheduleTypeInterface;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class InspectionScheduler implements ScheduleTypeInterface {

	public static int INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS = 90;
	
	long totalQuestions = 0;
	
	@Override
	public List<? extends ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext,boolean isUpdate, List<Map<String, Object>> parentRecordProps, boolean isManualOrScheduleTrigger) throws Exception {
		
		InspectionTemplateContext template = FieldUtil.getAsBeanFromMap(parentRecordProps.get(0), InspectionTemplateContext.class);
		
		long generatedUpto = baseScheduleContext.getGeneratedUptoTime() != null ? baseScheduleContext.getGeneratedUptoTime() : DateTimeUtil.getCurrenTime();
		
		long endDate = DateTimeUtil.getDayEndTimeOf(DateTimeUtil.addDays(DateTimeUtil.getCurrenTime(), INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS));
		
		if(generatedUpto < endDate) {
			
			List<InspectionResponseContext> responses = new ArrayList<InspectionResponseContext>();
			List<DateRange> times = baseScheduleContext.getScheduleInfo().getTimeIntervals(generatedUpto, endDate);
			
			totalQuestions = 0;
			
			if(template.getPages() != null) {
				totalQuestions = template.getPages().stream()
						.map(PageContext::getQuestions)
						.flatMap(List::stream)
						.count();
			}
			
			List<Long> resources = new ArrayList<Long>();
			
			if(template.getCreationType() == InspectionTemplateContext.CreationType.SINGLE.getIndex()) {
				if(template.getResource() != null) {
					resources.add(template.getResource().getId());
				}
				else {
					resources.add(null);
				}
			}
			else if(template.getCreationType() == InspectionTemplateContext.CreationType.MULTIPLE.getIndex())  {
				
				resources = getMultipleResource(template,baseScheduleContext);
			}
			
			for(DateRange time :times) {
				
				long createdtime = time.getEndTime()+1;
				
				for(Long resourceId : resources) {
					InspectionResponseContext response = template.constructResponse();

					response.setResStatus(ResponseContext.ResponseStatus.DISABLED); //This will be changed when the response is opened. Until then it can't be answered
					response.setCreatedTime(createdtime);
					response.setStatus(InspectionResponseContext.Status.PRE_OPEN.getIndex());
					response.setSourceType(InspectionResponseContext.SourceType.PLANNED.getIndex());
					response.setResource(getResource.apply(resourceId));
					responses.add(response);
				}
			}
			
			baseScheduleContext.setGeneratedUptoTime(endDate);
			
			V3VisitorManagementAPI.updateBaseScheduleContext(baseScheduleContext);
			
			return responses;
		}
		
		return null;
	}
	
	private List<Long> getMultipleResource(InspectionTemplateContext template, BaseScheduleContext baseScheduleContext) throws Exception {
		// TODO Auto-generated method stub
		
		Long baseSpaceId = template.getBaseSpace() != null ? template.getBaseSpace().getId() : template.getSiteId(); 
		Long spaceCategoryId = template.getSpaceCategory() != null ? template.getSpaceCategory().getId() : null;
		Long assetCategoryId = template.getAssetCategory() != null ? template.getAssetCategory().getId() : null;
		
		InspectionTriggerContext trigger = InspectionAPI.getInspectionTriggerByScheduer(baseScheduleContext.getId(), true).get(0);
		
		List<InspectionTriggerIncludeExcludeResourceContext> includeExcludeRess = trigger.getResInclExclList();
		
		List<Long> includedIds = null;
		List<Long> excludedIds = null;
		if(includeExcludeRess != null && !includeExcludeRess.isEmpty()) {
			for(InspectionTriggerIncludeExcludeResourceContext includeExcludeRes :includeExcludeRess) {
				if(includeExcludeRes.getIsInclude()) {
					includedIds = includedIds == null ? new ArrayList<>() : includedIds; 
					includedIds.add(includeExcludeRes.getResource().getId());
				}
				else {
					excludedIds = excludedIds == null ? new ArrayList<>() : excludedIds;
					excludedIds.add(includeExcludeRes.getResource().getId());
				}
			}
		}
		if(includedIds != null) {
			if(excludedIds != null) {
				includedIds.removeAll(excludedIds);
			}
			
			return includedIds;
		}
		
		List<Long> resources = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(template.getAssignmentTypeEnum(), baseSpaceId, spaceCategoryId, assetCategoryId, null, null, false);
		
		if(excludedIds != null) {
			resources.removeAll(excludedIds);
		}
		
		return resources;
	}

	Function<Long, ResourceContext> getResource = (resourceId) -> {
		
		if(resourceId != null) {
			ResourceContext resource = new ResourceContext();
			resource.setId(resourceId);
			return resource;
		}
		return null;
	};

	@Override
	public ScheduleType getSchedulerType() {
		// TODO Auto-generated method stub
		return ScheduleType.INSPECTION;
	}

}
