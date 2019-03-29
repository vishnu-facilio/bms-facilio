package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class LoadReportTypeChainCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String reportType = repContext.getReportType();

		if(reportType == null || reportType.isEmpty()) {
			throw new IllegalArgumentException("Report Type unknown");
		}
 		switch(reportType) {
 		case FacilioConstants.Reports.SUMMARY_REPORT_TYPE:
 	 		Chain summaryReportChain = ReportsChainFactory.getSummaryReportChain();
 	 		summaryReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE:
 	 		Chain topNSummaryReportChain = ReportsChainFactory.getTopNSummaryReportChain();
 	 		topNSummaryReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TOP_N_NUMERIC_REPORT_TYPE:
 			break;
 		case FacilioConstants.Reports.TOP_N_TABULAR_REPORT_TYPE:
 	 		Chain topNTabularReportChain = ReportsChainFactory.getTopNTabularReportChain();
 	 		topNTabularReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.NUMERIC_REPORT_TYPE:
 	 		Chain numericReportChain = ReportsChainFactory.getNumericReportChain();
 	 		numericReportChain.execute(repContext);
 			break;
 		case FacilioConstants.Reports.TREND_REPORT_TYPE:
 			break;
 		case FacilioConstants.Reports.TABULAR_REPORT_TYPE:
 	 		Chain tabularReportChain = ReportsChainFactory.getTabularReportChain();
 	 		tabularReportChain.execute(repContext);
 			break;
 		}
		
		return false;
	}
}
