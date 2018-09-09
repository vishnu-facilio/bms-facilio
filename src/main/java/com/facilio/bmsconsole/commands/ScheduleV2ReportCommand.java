package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;

public class ScheduleV2ReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long reportId = (long) context.get(FacilioConstants.ContextNames.REPORT_ID);
		if (reportId > 0) {
			FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
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
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule reportModule = modBean.getModule(moduleName);
			
			
			Map<String, Object> props=new HashMap<String,Object>();
			props.put("reportId", reportId);
			props.put("orgId", AccountUtil.getCurrentOrg().getId());
			props.put("moduleId", reportModule.getModuleId());
			props.put("fileFormat", fileFormat.getIntVal());
			props.put("templateId", emailTemplate.getId());
			
			FacilioModule module = ModuleFactory.getReportScheduleInfo();
			List<FacilioField> fields = FieldFactory.getReportScheduleInfo1Fields();
			
			ActivityType type = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			
			long jobId;
			if (type != ActivityType.EDIT) {
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
			
			FacilioTimer.scheduleCalendarJob(jobId, "ReportEmailScheduler", startTime, scheduleInfo, "facilio", maxCount != -1 ? maxCount : endTime);
			
		}
		return false;
	}

}
