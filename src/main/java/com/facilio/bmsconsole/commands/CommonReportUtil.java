package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseLineContext.AdjustType;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonReportUtil {
	public static void fetchBaseLines (ReportContext report, List<ReportBaseLineContext> reportBaseLines) throws Exception {
		if (reportBaseLines != null && !reportBaseLines.isEmpty()) {
			List<Long> baseLineIds = new ArrayList<>();
			for (ReportBaseLineContext baseLine : reportBaseLines) {
				if (baseLine.getBaseLineId() != -1) {
					baseLineIds.add(baseLine.getBaseLineId());
				}
				else {
					throw new IllegalArgumentException("Give valid baseline id");
				}
			}
			Map<Long, BaseLineContext> baseLines = BaseLineAPI.getBaseLinesAsMap(baseLineIds);
			
			if (baseLines == null || baseLines.isEmpty()) {
				throw new IllegalArgumentException("Give valid baseline id");
			}
			
			for (ReportBaseLineContext baseLine : reportBaseLines) {
				baseLine.setBaseLine(baseLines.get(baseLine.getBaseLineId()));
				
				if (baseLine.getAdjustTypeEnum() == null) {
					baseLine.setAdjustType(AdjustType.WEEK);
				}
				
				if (baseLine.getBaseLine() == null) {
					throw new IllegalArgumentException("Give valid baseline id. "+baseLine.getBaseLineId()+" is invalid");
				}
			}
			report.setBaseLines(reportBaseLines);
		}
	}
}
