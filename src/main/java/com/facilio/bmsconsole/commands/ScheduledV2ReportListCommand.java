package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class ScheduledV2ReportListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleToFetch = modBean.getModule(moduleName);
		
		FacilioModule module = ModuleFactory.getReportScheduleInfo();
		FacilioModule reportModule = ModuleFactory.getReportModule();
		List<FacilioField> fields = FieldFactory.getReportScheduleInfo1Fields();
		Map<String, FacilioField> reportFieldsMap = FieldFactory.getAsMap(FieldFactory.getReport1Fields());
		fields.add(reportFieldsMap.get("name"));
		fields.add(reportFieldsMap.get("id"));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.innerJoin(reportModule.getTableName())
				.on(module.getTableName()+".REPORTID = "+reportModule.getTableName()+".ID")
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module),String.valueOf(moduleToFetch.getModuleId()), NumberOperators.EQUALS));
		
		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (ids != null && !ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, ReportInfo> reportsMap = new HashMap<>();

		List<Long> reportInfoIds = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ReportInfo reportInfo = FieldUtil.getAsBeanFromMap(prop, ReportInfo.class);
				ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				reportInfo.setName(report.getName());
				reportInfo.setEmailTemplate((EMailTemplate) TemplateAPI.getTemplate(reportInfo.getTemplateId()));
//				report.setEmailTemplate(FieldUtil.getAsBeanFromMap(prop, EMailTemplate.class));
				reportInfoIds.add(reportInfo.getId());
				reportsMap.put(reportInfo.getId(), reportInfo);
			}
		}
		
		FacilioModule jobsModule = ModuleFactory.getJobsModule();
		List<FacilioField> jobsField = FieldFactory.getJobFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(jobsField);
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(jobsField)
				.table(jobsModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(jobsModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobId"), reportInfoIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobName"), "ReportEmailScheduler", StringOperators.IS));
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		Map<Long, JobContext> jobMap = new HashMap<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for(Map<String, Object> prop : jobProps) {
				JobContext job = FieldUtil.getAsBeanFromMap(prop, JobContext.class);
				jobMap.put(job.getJobId(), job);
			}
		}
		
		Iterator<Map.Entry<Long, ReportInfo>> itr = reportsMap.entrySet().iterator();
		
		while (itr.hasNext()) {
			Map.Entry<Long, ReportInfo> entry = itr.next();
			ReportInfo info = entry.getValue();
			if (jobMap.containsKey(info.getId())) {
				JobContext job = jobMap.get(info.getId());
				if (job.isActive()) {
					reportsMap.get(info.getId()).setJob(job);
				}
				else {
					itr.remove();
				}
			}
			else {
				itr.remove();
				// TODO delete removed job entries from info table
			}
		}
		
		List<ReportInfo> reports = reportsMap.values().stream().collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.REPORT_LIST, reports);
				
		return false;
	}

}
