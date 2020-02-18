package com.facilio.bmsconsole.commands;

import java.util.List;

//import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.FilterUtil;

public class FetchCriteriaReportCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(FetchCriteriaReportCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean needCriteriaReport =  (boolean) context.get(FacilioConstants.ContextNames.NEED_CRITERIAREPORT);
		if(needCriteriaReport) {
			JSONObject dataFilter = (JSONObject) context.get(FacilioConstants.ContextNames.DATA_FILTER);
			if(dataFilter != null && !dataFilter.isEmpty()) {
				ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
				List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
				
				List<ReportDataPointContext> dataPoints = FilterUtil.getDFDataPoints(dataFilter);
				reportDataPoints.addAll(dataPoints);
				report.setDataPoints(reportDataPoints);
			}
//			JSONObject timeFilter = (JSONObject) context.get(FacilioConstants.ContextNames.TIME_FILTER);
//			if(timeFilter != null && !timeFilter.isEmpty()) {
//				ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
//				List<ReportDataPointContext> reportDataPoints = report.getDataPoints();
				
//				List<ReportDataPointContext> dataPoints = FilterUtil.getTFDataPoints(timeFilter);
//				reportDataPoints.addAll(dataPoints);
//				report.setDataPoints(reportDataPoints);
			}
//		}
		return false;
	}
	
}