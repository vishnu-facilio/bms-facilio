package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.time.DateRange;


public class FormatHeatMapDataCommand extends FacilioCommand{
	
	private static final Logger LOGGER = Logger.getLogger(FormatHeatMapDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		long start=System.currentTimeMillis();
		
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if(reportContext.getAnalyticsType() == 3 && reportContext.getType() == 1) {
			Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			
			DateRange range = reportContext.getDateRange();
			SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH");
			Long startTime=range.getStartTime();
			Long endTime=range.getEndTime();
			List<Map<String, Object>> heatMapData = new ArrayList<>();
			long timeStep = endTime-startTime;
			if(reportContext.getxAggr() == 20) {
				timeStep = 3600000;
			}
			else if(reportContext.getxAggr() == 12) {
				sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				timeStep = 86400000;
			}
			
			long loopstart=System.currentTimeMillis();
				while(startTime<=endTime) {
					HashMap mapData= new HashMap();
					mapData.put("X", startTime);
					mapData.put("Y",startTime);
					Date date = new java.util.Date(startTime);
					for(Map<String, Object> record : data) {
						Date recordDate = new java.util.Date(((long) record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS)));
						if((sdf.format(date)).equalsIgnoreCase(sdf.format(recordDate))) {
							record.put("Y", record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS));
							mapData= (HashMap) record;
						}
					}
					startTime=startTime+timeStep;
					heatMapData.add(mapData);
					}
			reportData.put("heatMapData", heatMapData);
			
		long loopend=System.currentTimeMillis()- loopstart;
		long commandend=System.currentTimeMillis()-start;
		
		LOGGER.info("time taken for loop  -->  "+loopend);
		LOGGER.info("time taken for command  -->  "+commandend);
			
		}
		
		
		return false;
	}
}