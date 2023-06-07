package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportPivotParamsContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReportEmailScheduler extends FacilioJob {
	
	private Logger LOGGER = LogManager.getLogger(ReportEmailScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		long jobId = jc.getJobId();
		try {
			FacilioModule module = ModuleFactory.getReportScheduleInfo();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(FieldFactory.getReportScheduleInfo1Fields())
					.andCondition(CriteriaAPI.getIdCondition(jobId, module));
			List<Map<String, Object>> props = builder.get();
			if(props != null && !props.isEmpty() && props.get(0) != null) {
				Map<String, Object> prop = props.get(0);
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.JOB_ID,jobId);
				FileFormat fileFormat = FileFormat.getFileFormat((int) prop.get("fileFormat"));
				context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
				
				EMailTemplate template  = (EMailTemplate) TemplateAPI.getTemplate((long)prop.get("templateId"));
				context.put(FacilioConstants.Workflow.TEMPLATE, template);
				
				Long reportId = (long) prop.get("reportId");
				ReportContext reportContext = ReportUtil.getReport(reportId);
				
				context.put(FacilioConstants.ContextNames.REPORT, reportContext);
				context.put("isS3Url", true);
				
				FacilioChain mailReportChain;
				if (reportContext.getTypeEnum() == ReportType.WORKORDER_REPORT) {
					mailReportChain = TransactionChainFactory.sendModuleReportMailChain(fileFormat != FileFormat.IMAGE && fileFormat != FileFormat.PDF);
				} else if (reportContext.getTypeEnum() == ReportType.PIVOT_REPORT) {
					ReportUtil.Constructpivot(context,jobId);
					mailReportChain = TransactionChainFactory.sendPivotReportMailChain();
				} else {
					mailReportChain = TransactionChainFactory.sendReportMailChain();
				}
				mailReportChain.execute(context);}
		} catch (Exception e) {
			LOGGER.error("Error occurred during execution of ReportEmailScheduler", e);
			CommonCommandUtil.emailException(ReportEmailScheduler.class.getName(), "Report Email Scheduler Job failed", e);
		}
	}

}
