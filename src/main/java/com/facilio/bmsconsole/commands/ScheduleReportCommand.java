package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long reportId = (long) context.get(FacilioConstants.ContextNames.REPORT_ID);
		if (reportId > 0) {
			// JSONArray dateFilter = (JSONArray)context.get(FacilioConstants.ContextNames.DATE_FILTER);
			int fileFormat = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
			EMailTemplate emailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
			
			long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
			ScheduleInfo scheduleInfo = (ScheduleInfo) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
			long endTime = -1;
			if ( context.containsKey(FacilioConstants.ContextNames.END_TIME)) {
				endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
			}
			int maxCount = -1;
			if ( context.containsKey(FacilioConstants.ContextNames.MAX_COUNT)) {
				maxCount = (int) context.get(FacilioConstants.ContextNames.MAX_COUNT);
			}
			
			Map<String, Object> props=new HashMap<String,Object>();
			props.put("reportId", reportId);
			props.put("fileFormat", fileFormat);
			props.put("templateId", emailTemplate.getId());
			
			FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
			List<FacilioField> fields = FieldFactory.getReportScheduleInfoFields();
			
			EventType type = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			
			long jobId;
			if (type != EventType.EDIT) {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(module.getTableName())
						.fields(fields);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				jobId = (long) props.get("id");
			}
			else {
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				jobId = recordIds.get(0);
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(module.getTableName())
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(jobId, module));

				int count = updateBuilder.update(props);
				
				context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
				
				Long templateId = (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
				TemplateAPI.deleteTemplate(templateId);
				
			}
			
			if (maxCount != -1) {
				FacilioTimer.scheduleCalendarJob(jobId, "ReportScheduler", startTime, scheduleInfo, "facilio", maxCount);
			}
			else {
				FacilioTimer.scheduleCalendarJob(jobId, "ReportScheduler", startTime, scheduleInfo, "facilio", endTime);
			}
		}
		return false;
	}
	
}
