package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.executor.ScheduleInfo;

public class ScheduleReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long reportId = (long) context.get(FacilioConstants.ContextNames.REPORT_ID);
		if (reportId > 0) {
			JSONArray dateFilter = (JSONArray)context.get(FacilioConstants.ContextNames.DATE_FILTER);
			int fileFormat = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
			EMailTemplate emailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
			
			long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
			ScheduleInfo scheduleInfo = (ScheduleInfo) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getReportScheduleInfoModule().getTableName())
					.fields(FieldFactory.getReportScheduleInfoFields());

			Map<String, Object> props=new HashMap<String,Object>();
			props.put("reportId", reportId);
			props.put("dateFilter", dateFilter);
			props.put("fileFormat", fileFormat);
			props.put("templateId", emailTemplate.getId());

			insertBuilder.addRecord(props);
			insertBuilder.save();

			long jobId = (long) props.get("id");
			FacilioTimer.scheduleCalendarJob(jobId, "ReportScheduler", startTime, scheduleInfo, "facilio");
		}
		return false;
	}

}
