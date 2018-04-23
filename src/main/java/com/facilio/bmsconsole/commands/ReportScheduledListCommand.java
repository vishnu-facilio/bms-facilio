package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.JobContext;

public class ReportScheduledListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleToFetch = modBean.getModule(moduleName);
		
		FacilioModule module = ModuleFactory.getReportScheduleInfoModule();
		FacilioModule reportModule = ModuleFactory.getReport();
		FacilioModule emailModule = ModuleFactory.getEMailTemplatesModule();
		
		List<FacilioField> fields = FieldFactory.getReportScheduleInfoFields();
		fields.addAll(FieldFactory.getReportFields());
//		fields.addAll(FieldFactory.getEMailTemplateFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.innerJoin(reportModule.getTableName())
				.on(module.getTableName()+".REPORTID = "+reportModule.getTableName()+".ID")
/*				.innerJoin(emailModule.getTableName())
				.on(module.getTableName()+".TEMPLATEID = "+emailModule.getTableName()+".ID")*/
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportModule))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(reportModule),String.valueOf(moduleToFetch.getModuleId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, ReportInfo> reportsMap = new HashMap<>();

		List<Long> reportIds = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ReportInfo report = FieldUtil.getAsBeanFromMap(prop, ReportInfo.class);
				report.setReportContext(FieldUtil.getAsBeanFromMap(prop, ReportContext.class));
				report.setEmailTemplate((EMailTemplate) TemplateAPI.getTemplate(report.getTemplateId()));
//				report.setEmailTemplate(FieldUtil.getAsBeanFromMap(prop, EMailTemplate.class));
				reportIds.add(report.getId());
				reportsMap.put(report.getId(), report);
			}
		}
		
		FacilioModule jobsModule = ModuleFactory.getJobsModule();
		List<FacilioField> jobsField = FieldFactory.getJobFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(jobsField);
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(jobsField)
				.table(jobsModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(jobsModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobId"), reportIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobName"), "ReportScheduler", StringOperators.IS));
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			for(Map<String, Object> prop : jobProps) {
				JobContext job = FieldUtil.getAsBeanFromMap(prop, JobContext.class);
				if (job.isActive()) {
					reportsMap.get(job.getJobId()).setJob(job);
				}
				else {
					reportsMap.remove(job.getJobId());
				}
			}
		}
		
		List<ReportInfo> reports = reportsMap.values().stream().collect(Collectors.toList());
		context.put(FacilioConstants.ContextNames.REPORT_LIST, reports);
				
		return false;
	}

}
