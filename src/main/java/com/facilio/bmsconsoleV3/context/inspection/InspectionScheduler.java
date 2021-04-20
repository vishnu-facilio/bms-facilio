package com.facilio.bmsconsoleV3.context.inspection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.bmsconsole.context.ScheduleTypeInterface;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.PageContext;
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
			
			times.stream().forEach(
				(time) -> 
				{
					long createdtime = time.getEndTime()+1;
					
					InspectionResponseContext response = new InspectionResponseContext();
				
					response.setCreatedTime(createdtime);
					response.setParent(template);
					response.setTotalQuestions((int) totalQuestions);
					response.setSiteId(template.getSiteId());
					response.setSite(template.getSite());
					response.setStatus(InspectionResponseContext.Status.PRE_OPEN.getVal());
					
					responses.add(response);
				}
			);
			
			baseScheduleContext.setGeneratedUptoTime(endDate);
			
			V3VisitorManagementAPI.updateBaseScheduleContext(baseScheduleContext);
			
			return responses;
		}
		
		return null;
	}

	@Override
	public ScheduleType getSchedulerType() {
		// TODO Auto-generated method stub
		return ScheduleType.INSPECTION;
	}

}
