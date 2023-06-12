package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.report.V3ScheduledReportRelContext;
import com.facilio.command.FacilioCommand;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.JobContext;

;

public class ScheduledV2ReportListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleToFetch = modBean.getModule(moduleName);
		List<Long> moduleIds = new ArrayList<>();
		if(!moduleName.equals("pivot")){
			String name = AccountUtil.getCurrentApp().getLinkName();
			if(name.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
				if(moduleName != null && moduleName.equals("custom")){
					List<FacilioModule> moduleList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY,true);
					for (FacilioModule prop : moduleList) {
						moduleIds.add(prop.getModuleId());
					}
				}
				else{
					moduleIds.add(moduleToFetch.getModuleId());
				}
			}
			else{
				moduleIds.add(moduleToFetch.getModuleId());
			}
		}

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
				.on(module.getTableName()+".REPORTID = "+reportModule.getTableName()+".ID");
				//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				if(moduleName != null && moduleName.equals("pivot") )
				{
					selectBuilder.andCondition(CriteriaAPI.getCondition(module.getTableName()+".REPORT_TYPE","reportType", ReportContext.ReportType.PIVOT_REPORT.getValue()+"", NumberOperators.EQUALS));
				}else {
					selectBuilder.andCondition(CriteriaAPI.getCondition(module.getTableName()+".REPORT_TYPE","reportType", ReportContext.ReportType.PIVOT_REPORT.getValue()+"", NumberOperators.NOT_EQUALS));
				}

		List<Long> ids = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (ids != null && !ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}

		List<Map<String, Object>> props = new ArrayList<>();
		List<Map<String, Object>> lists = selectBuilder.get();
		if(!moduleName.equals("pivot")){
			for(Map<String, Object> list : lists){
				if(list.get("moduleId") != null && moduleIds.contains(list.get("moduleId"))) {
					props.add(list);
				}
			}
		}
		else{
			props.addAll(lists);
		}
		Map<Long, ReportInfo> reportsMap = new HashMap<>();

		List<Long> reportInfoIds = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props){
				ReportInfo reportInfo = FieldUtil.getAsBeanFromMap(prop, ReportInfo.class);
				ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				reportInfo.setName(report.getName());
				reportInfo.setEmailTemplate((EMailTemplate) TemplateAPI.getTemplate(reportInfo.getTemplateId()));
				//				report.setEmailTemplate(FieldUtil.getAsBeanFromMap(prop, EMailTemplate.class));
				reportInfoIds.add(reportInfo.getId());
				reportsMap.put(reportInfo.getId(), reportInfo);
				if(reportInfo.getScheduled_report_name() != null)
				{
					reportInfo.setName(reportInfo.getScheduled_report_name());
				}
			}
		}


		if(reportInfoIds != null && !reportInfoIds.isEmpty()) {

			List<JobContext> jcs = FacilioTimer.getActiveJobs(reportInfoIds, "ReportEmailScheduler");
			Map<Long, JobContext> jobMap = jcs.stream().collect(Collectors.toMap(JobContext::getJobId, Function.identity()));

			Iterator<Map.Entry<Long, ReportInfo>> itr = reportsMap.entrySet().iterator();

			while (itr.hasNext()) {
				Map.Entry<Long, ReportInfo> entry = itr.next();
				ReportInfo info = entry.getValue();
				if (jobMap.containsKey(info.getId())) {
					JobContext job = jobMap.get(info.getId());
					reportsMap.get(info.getId()).setJob(job);
				}
				else {
					itr.remove();
					// TODO delete removed job entries from info table
				}
			}
		}
		List<ReportInfo> reports = reportsMap.values().stream().collect(Collectors.toList());
		ReportUtil.getScheduledInfo1Rel(reports);
		context.put(FacilioConstants.ContextNames.REPORT_LIST, reports);

		return false;
	}

}
