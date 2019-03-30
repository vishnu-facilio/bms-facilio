package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ReportScheduler extends FacilioJob {

	private Logger log = LogManager.getLogger(ReportScheduler.class.getName());

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
				
				EMailTemplate template  = (EMailTemplate) TemplateAPI.getTemplate((long)prop.get("templateId"));
				context.put(FacilioConstants.Workflow.TEMPLATE, template);
				
				Long reportId = (long) prop.get("reportId");
				ReportContext reportContext = DashboardUtil.getReportContext(reportId);
				context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
				
				DashboardAction action = new DashboardAction();
				action.setReportContext(reportContext);
				action.getData();
				context.put(FacilioConstants.ContextNames.REPORT, action.getReportData());
				if (reportContext.getReportChartType() == ReportContext.ReportChartType.TABULAR) {
					context.put(FacilioConstants.ContextNames.REPORT_COLUMN_LIST, action.getReportColumns());
				}
				else {
					context.put(FacilioConstants.ContextNames.BASE_LINE, action.getBaseLineComparisionDiff());
					context.put(FacilioConstants.ContextNames.FILTERS, action.getReportSpaceFilterContext());
					context.put(FacilioConstants.ContextNames.DATE_FILTER, action.getDateFilter());
				}
				
				FacilioModule module = ReportsUtil.getReportModule(reportContext);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
//				context.put(FacilioConstants.ContextNames.CV_NAME, reportId.toString());
//				context.put(FacilioConstants.ContextNames.PARENT_VIEW, "report");
				context.put(FacilioConstants.ContextNames.LIMIT_VALUE, -1);
								
				Chain mailReportChain = ReportsChainFactory.getSendMailReportChain();
				mailReportChain.execute(context);
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		
	}
	
}