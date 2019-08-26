package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.chain.Context;
import org.apache.commons.math3.util.FastMath;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;

public class FormatHeatMapDataCommand extends FacilioCommand{

	private static final Logger LOGGER = Logger.getLogger(FormatHeatMapDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if(reportContext.getAnalyticsType() == 3) {
			Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			LOGGER.severe("reportData --- "+ reportData.get("data"));
			for(Map<String, Object> record : data) {
				record.put("Y", record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS));
			}
			
		}
		
		
		return false;
	}
}