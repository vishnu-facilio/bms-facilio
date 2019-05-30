package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetWoScheduledViewList implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleToFetch = modBean.getModule(moduleName);
		long moduleId = modBean.getModule(moduleName).getModuleId();
		FacilioModule module = ModuleFactory.getViewScheduleInfoModule();
		ModuleFactory.getEMailTemplatesModule();
		
		List<FacilioField> fields = FieldFactory.getViewScheduleInfoFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
				.andCustomWhere("ORGID = ? AND MODULEID = ?", AccountUtil.getCurrentOrg().getOrgId(), moduleId);

		
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, ReportInfo> woReportsMap = new HashMap<>();

		List<Long> reportIds = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				ReportInfo report = FieldUtil.getAsBeanFromMap(prop, ReportInfo.class);
				report.setReportContext(FieldUtil.getAsBeanFromMap(prop, ReportContext.class));
				report.setEmailTemplate((EMailTemplate) TemplateAPI.getTemplate(report.getTemplateId()));
				reportIds.add(report.getId());
				woReportsMap.put(report.getId(), report);
			}
		}
		if (!reportIds.isEmpty()) {
			FacilioModule jobsModule = ModuleFactory.getJobsModule();
			List<FacilioField> jobsField = FieldFactory.getJobFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(jobsField);
			 
			selectBuilder = new GenericSelectRecordBuilder()
					.select(jobsField)
					.table(jobsModule.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(jobsModule))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobId"), reportIds, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobName"), "ViewEmailScheduler", StringOperators.IS));
			
			List<Map<String, Object>> jobProps = selectBuilder.get();
			if(jobProps != null && !jobProps.isEmpty()) {
				for(Map<String, Object> prop : jobProps) {
					JobContext job = FieldUtil.getAsBeanFromMap(prop, JobContext.class);
					if (job.isActive()) {
						woReportsMap.get(job.getJobId()).setJob(job);
					}
					else {
						woReportsMap.remove(job.getJobId());
					}
				}
			}	
		}
		List<ReportInfo> woReports = woReportsMap.values().stream().collect(Collectors.toList());
//		FacilioModule module1 = ModuleFactory.getViewsModule();
//		List<FacilioField> fields1 = FieldFactory.getViewFields();
//		GenericSelectRecordBuilder selectBuilder1 = new GenericSelectRecordBuilder()
//				.select(fields1)
//				.table(module1.getTableName());
//		List<Map<String, Object>> viewProps = selectBuilder.get();
//				System.out.println("12323" + selectBuilder1.get());
//				
//				for (int i = 0; i < woReports.size(); i++) {
//					for (int j = 0; j < viewProps.size(); j++) {
//					if (woReports.get(i).getReportContext().getData().) {
//						((Map) woReports).put("", viewProps.get(j));
//					}
//					
//					
//				}
		
				
				context.put(FacilioConstants.ContextNames.REPORT_LIST, woReports);
		return false;
	}

}
