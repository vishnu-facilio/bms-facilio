package com.facilio.bmsconsoleV3.context.inspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ScheduleTypeInterface;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.util.BulkResourceAllocationUtil;
import com.facilio.bmsconsoleV3.util.InspectionAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.ResponseContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;

@Log4j
public class InspectionScheduler implements ScheduleTypeInterface {

	public static int INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS = 30;
	
	@Override
	public List<? extends ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext,boolean isUpdate, List<Map<String, Object>> parentRecordProps, boolean isManualOrScheduleTrigger) throws Exception {
		
		Long inspectionId = (Long)parentRecordProps.get(0).get("id");

		LOGGER.info("Creating Inspection Response for Template ID : "+inspectionId);
		FacilioContext context = V3Util.getSummary(FacilioConstants.Inspection.INSPECTION_TEMPLATE, Collections.singletonList(inspectionId));
		
		InspectionTemplateContext template = (InspectionTemplateContext) Constants.getRecordList((FacilioContext) context).get(0);
		
		if(template.getStatus().equals(Boolean.FALSE)) {
			return null;
		}
		
		long generatedUpto = baseScheduleContext.getGeneratedUptoTime() != null ? baseScheduleContext.getGeneratedUptoTime() : DateTimeUtil.getCurrenTime();
		
		long endDate = DateTimeUtil.getDayEndTimeOf(DateTimeUtil.addDays(DateTimeUtil.getCurrenTime(), INSPECTION_PRE_GENERATE_INTERVAL_IN_DAYS));

		LOGGER.info("Inspection Generated Upto Time : "+generatedUpto+" and End Date : "+endDate);

		if(generatedUpto < endDate) {
			
			List<DateRange> times = baseScheduleContext.getScheduleInfo().getTimeIntervals(generatedUpto, endDate);

			LOGGER.info("Times for which Inspection Response to be generated : "+times);

			List<InspectionResponseContext> responses = new ArrayList<InspectionResponseContext>();
			
			List<ResourceContext> resources = new ArrayList<ResourceContext>();
			
			if(template.getCreationType() == InspectionTemplateContext.CreationType.SINGLE.getIndex()) {
				if(template.getResource() != null) {
					resources.add(ResourceAPI.getResource(template.getResource().getId()));
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
				
				for(ResourceContext resource : resources) {
					InspectionResponseContext response = template.constructResponse();

					response.setResStatus(ResponseContext.ResponseStatus.DISABLED); //This will be changed when the response is opened. Until then it can't be answered
					response.setCreatedTime(createdtime);
					response.setScheduledWorkStart(createdtime);
					response.setStatus(InspectionResponseContext.Status.PRE_OPEN.getIndex());
					response.setSourceType(InspectionResponseContext.SourceType.PLANNED.getIndex());
					response.setResource(resource);
					response.setSiteId(resource.getSiteId());
					responses.add(response);
				}
			}
			
			baseScheduleContext.setGeneratedUptoTime(endDate);
			
			V3VisitorManagementAPI.updateBaseScheduleContext(baseScheduleContext);

			LOGGER.info("Count of Inspection Responses to be Created : "+ responses.size());

			return responses;
		}
		
		return null;
	}
	
	public List<InspectionResponseContext> getResponses(InspectionTemplateContext template,BaseScheduleContext baseScheduleContext,List<DateRange> times) throws Exception {
		
		
		
		List<InspectionResponseContext> responses = new ArrayList<InspectionResponseContext>();
		
		List<ResourceContext> resources = new ArrayList<ResourceContext>();
		
		if(template.getCreationType() == InspectionTemplateContext.CreationType.SINGLE.getIndex()) {
			if(template.getResource() != null) {
				resources.add(ResourceAPI.getResource(template.getResource().getId()));
			}
		}
		else if(template.getCreationType() == InspectionTemplateContext.CreationType.MULTIPLE.getIndex())  {
			
			resources = getMultipleResource(template,baseScheduleContext);
		}
		
		for(DateRange time :times) {
			
			long createdtime = time.getEndTime()+1;
			
			for(ResourceContext resource : resources) {
				InspectionResponseContext response = template.constructResponse();

				response.setResStatus(ResponseContext.ResponseStatus.DISABLED); //This will be changed when the response is opened. Until then it can't be answered
				response.setCreatedTime(createdtime);
				response.setStatus(InspectionResponseContext.Status.PRE_OPEN.getIndex());
				response.setSourceType(InspectionResponseContext.SourceType.PLANNED.getIndex());
				response.setResource(resource);
				response.setSiteId(resource.getSiteId());
				responses.add(response);
			}
		}
		
		return responses;
	}
	
	private List<ResourceContext> getMultipleResource(InspectionTemplateContext template, BaseScheduleContext baseScheduleContext) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> baseSpaceIds = null;
		
		if(template.getBuildings() != null) {
			baseSpaceIds = template.getBuildings().stream().map(BuildingContext::getId).collect(Collectors.toList());
		}
		else {
			baseSpaceIds = template.getSites().stream().map(SiteContext::getId).collect(Collectors.toList());
		}
		
		Long spaceCategoryId = template.getSpaceCategory() != null ? template.getSpaceCategory().getId() : null;
		Long assetCategoryId = template.getAssetCategory() != null ? template.getAssetCategory().getId() : null;
		
		List<Long> includedIds = null;
		List<Long> excludedIds = null;
		
		if(baseScheduleContext != null) {
			
			InspectionTriggerContext trigger = InspectionAPI.getInspectionTriggerByScheduer(baseScheduleContext.getId(), true).get(0);
			
			List<InspectionTriggerIncludeExcludeResourceContext> includeExcludeRess = trigger.getResInclExclList();
			
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
				
				List<ResourceContext> includeResources = ResourceAPI.getResources(includedIds, false);
				return includeResources;
			}
		}
		
		List<ResourceContext> resources = BulkResourceAllocationUtil.getMultipleResourceToBeAddedFromPM(template.getAssignmentTypeEnum(), baseSpaceIds, spaceCategoryId, assetCategoryId, null, null, false);
		
		if(excludedIds != null) {
			List<ResourceContext> tempResourceContext = new ArrayList<ResourceContext>();
			
			for(ResourceContext resourceContext : resources) {
				
				if(!excludedIds.contains(resourceContext.getId())) {
					tempResourceContext.add(resourceContext);
				}
			}
			resources = tempResourceContext;
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
