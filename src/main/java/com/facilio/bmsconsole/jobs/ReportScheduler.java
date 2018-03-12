package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ReportScheduler extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		
		long jobId = jc.getJobId();
		try {
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getReportScheduleInfoModule().getTableName())
					.select(FieldFactory.getReportScheduleInfoFields())
					.andCustomWhere("ID = ?", jobId);
			List<Map<String, Object>> props = builder.get();
			if(props != null && !props.isEmpty() && props.get(0) != null) {
				Map<String, Object> prop = props.get(0);
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.FILE_FORMAT, prop.get("fileFormat"));
				/*String filter = (String) prop.get("dateFilter");
				JSONArray dateFilter = null;
				if (filter != null) {
					JSONParser parser = new JSONParser();
					try {
						dateFilter =  (JSONArray) parser.parse(filter);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				context.put(FacilioConstants.ContextNames.DATE_FILTER, dateFilter);*/
				
				EMailTemplate template  = (EMailTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getId(), (long)prop.get("templateId"));
				context.put(FacilioConstants.Workflow.TEMPLATE, template);
				
				Long reportId = (long) prop.get("reportId");
				ReportContext reportContext = DashboardUtil.getReportContext(reportId);
				context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
				
				FacilioModule module = ReportsUtil.getReportModule(reportContext);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
				context.put(FacilioConstants.ContextNames.CV_NAME, reportId.toString());
				context.put(FacilioConstants.ContextNames.PARENT_VIEW, "report");
								
				Chain mailReportChain = ReportsChainFactory.getSendMailReportChain();
				mailReportChain.execute(context);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}