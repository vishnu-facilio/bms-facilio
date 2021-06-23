package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class LoadReportTypeChainCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String reportType = repContext.getReportType();

		if(reportType == null || reportType.isEmpty()) {
			throw new IllegalArgumentException("Report Type unknown");
		}
 		switch(reportType) {
 		case FacilioConstants.Reports.SUMMARY_REPORT_TYPE:
 	 		FacilioChain summaryReportChain = ReportsChainFactory.getSummaryReportChain();
 	 		summaryReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE:
 	 		FacilioChain topNSummaryReportChain = ReportsChainFactory.getTopNSummaryReportChain();
 	 		topNSummaryReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TOP_N_NUMERIC_REPORT_TYPE:
 			break;
 		case FacilioConstants.Reports.TOP_N_TABULAR_REPORT_TYPE:
 	 		FacilioChain topNTabularReportChain = ReportsChainFactory.getTopNTabularReportChain();
 	 		topNTabularReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.NUMERIC_REPORT_TYPE:
 	 		FacilioChain numericReportChain = ReportsChainFactory.getNumericReportChain();
 	 		numericReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TREND_REPORT_TYPE:
 			break;
 		case FacilioConstants.Reports.TABULAR_REPORT_TYPE:
 	 		FacilioChain tabularReportChain = ReportsChainFactory.getTabularReportChain();
 	 		tabularReportChain.execute(repContext);
 			break;
 		}
		
		return false;
	}
}
