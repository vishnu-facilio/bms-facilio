package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.report.V3ScheduledReportRelContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;

public class ScheduleV2ReportCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReportInfo reportInfo = (ReportInfo) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
		
		
		String moduleName = reportInfo.getModuleName();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule reportModule = modBean.getModule(moduleName);
		
		FileFormat fileFormat = reportInfo.getFileFormatEnum();
		EMailTemplate emailTemplate = reportInfo.getEmailTemplate();
		
		Map<String, Object> props=new HashMap<String,Object>();
		props.put("reportId", reportInfo.getReportId());
		props.put("orgId", AccountUtil.getCurrentOrg().getId());
		props.put("moduleId", reportModule.getModuleId());
		props.put("fileFormat", fileFormat.getIntVal());
		props.put("templateId", emailTemplate.getId());
		if(reportInfo.getScheduled_report_name() != null){
			props.put("scheduled_report_name", reportInfo.getScheduled_report_name());
		}
		ReportContext reportContext = ReportUtil.getReport(reportInfo.getReportId());
		if(reportContext != null)
		{
			props.put("reportType" , reportContext.getTypeEnum().getValue());
		}
		FacilioModule module = ModuleFactory.getReportScheduleInfo();
		List<FacilioField> fields = FieldFactory.getReportScheduleInfo1Fields();
		
		EventType type = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		
		long jobId;
		if (type != EventType.EDIT) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields);

			insertBuilder.addRecord(props);
			insertBuilder.save();
			jobId = (long) props.get("id");
			context.put(FacilioConstants.ContextNames.RECORD_ID, jobId);
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
		}
		if(reportInfo.getSelected_reportIds() != null && reportInfo.getSelected_reportIds().size() > 0)
		{
			insertScheduledRelatedRel(reportInfo.getSelected_reportIds() , jobId, type);
		}
		long startTime = reportInfo.getStartTime();
		ScheduleInfo scheduleInfo = reportInfo.getScheduleInfo();
		long endTime = reportInfo.getEndTime();
		int maxCount = reportInfo.getMaxCount();
		if (maxCount != -1) {
			FacilioTimer.scheduleCalendarJob(jobId, "ReportEmailScheduler", startTime, scheduleInfo, "facilio",maxCount);			
		}
		else {
			FacilioTimer.scheduleCalendarJob(jobId, "ReportEmailScheduler", startTime, scheduleInfo, "facilio",endTime);
		}
		return false;
	}

	private void insertScheduledRelatedRel(List<Long> selected_report_ids, Long scheduled_id, EventType type)throws Exception
	{
		if(type == EventType.EDIT && scheduled_id > 0)
		{
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getReportScheduleInfoRel().getTableName())
					.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", AccountUtil.getCurrentOrg().getId() +"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("SCHEDULED_ID", "scheduled_id", scheduled_id +"", NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
		if(scheduled_id != null && scheduled_id > 0)
		{
			V3ScheduledReportRelContext scheduledRelContext = null;
			for(Long reportId : selected_report_ids)
			{
				scheduledRelContext = new V3ScheduledReportRelContext();
				scheduledRelContext.setScheduled_id(scheduled_id);
				scheduledRelContext.setReportId(reportId);
				scheduledRelContext.setOrgId(AccountUtil.getCurrentOrg().getId());

				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getReportScheduleInfoRel().getTableName())
						.fields(FieldFactory.getReportScheduleInfo1RelFields());

				insertBuilder.insert(FieldUtil.getAsProperties(scheduledRelContext));
			}
		}
	}
}
