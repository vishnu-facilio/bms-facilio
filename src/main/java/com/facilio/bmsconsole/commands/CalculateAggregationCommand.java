package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class CalculateAggregationCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CalculateAggregationCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean isCalculate = (Boolean) context.get(FacilioConstants.ContextNames.CALCULATE_REPORT_AGGR_DATA);
		if (isCalculate != null && !isCalculate) {
			return false;
		}
		
		long startTime = System.currentTimeMillis();
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		String sortAlias = (String) context.get(FacilioConstants.ContextNames.REPORT_SORT_ALIAS);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (reportData != null && !reportData.isEmpty()) {
			List<Map<String, Object>> csvData = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
			
			Map<String, Object> aggrData = new HashMap<>();
			
			
			for (ReportDataPointContext dp : report.getDataPoints()) {
				if (!dp.isAggrCalculated()) {
					switch (dp.getyAxis().getDataTypeEnum()) {
						case NUMBER:
						case DECIMAL:
							doDecimalAggr(dp, csvData, sortAlias, aggrData);
							break;
						case BOOLEAN:
						case ENUM:
							if (dp.isHandleEnum()) {
								doEnumAggr(dp, csvData, aggrData);
							}
							break;
						default:
							break;
					}
					dp.setAggrCalculated(true);
				}
			}
			
			if (reportAggrData == null) {
				reportData.put(FacilioConstants.ContextNames.AGGR_KEY, aggrData);
			}
			else {
				reportAggrData.putAll(aggrData);
			}
		}
		LOGGER.debug("Time taken for calculating aggregation is : "+(System.currentTimeMillis() - startTime));
		return false;
	}
	
	private void doEnumAggr (ReportDataPointContext dp, List<Map<String, Object>> csvData, Map<String, Object> aggrData) {
		for (Map<String, Object> data : csvData) {
			for (String alias : dp.getAliases().values()) {
				calculateEnumAggr(data.get(alias), alias, aggrData);
			}
		}
	}
	
	private void calculateEnumAggr (Object value, String alias, Map<String, Object> aggrData) {
		if (value != null) {
			String timelineKey = alias+".timeline";
		}
	}
	
	private void doDecimalAggr (ReportDataPointContext dp, List<Map<String, Object>> csvData, String sortAlias, Map<String, Object> aggrData) {
		for (Map<String, Object> data : csvData) {
			boolean isLatest = false;
			if (sortAlias != null && !sortAlias.isEmpty()) {
				Double sortVal = getDoubleVal(data.get(sortAlias));
				if (sortVal != null) {
					Double currentSortVal = (Double) aggrData.get(sortAlias+".lastValue");
					isLatest = currentSortVal == null || currentSortVal <= sortVal;
				}
			}
			
			for (String alias : dp.getAliases().values()) {
				calculateNumericAggr(data.get(alias), alias, aggrData, isLatest);
			}
		}
	}
	
	private void calculateNumericAggr (Object value, String alias, Map<String, Object> aggrData, boolean isLatest) {
		Double val = getDoubleVal(value);
		if (val != null) {
			String sumKey = alias+".sum";
			Double sum = (Double) aggrData.get(sumKey); 
			if (sum == null) {
				addFirstTimeAggr(alias, val, aggrData);
			}
			else {
				sum = sum + val; 
				aggrData.put(sumKey, sum);
				
				String minKey = alias+".min";
				if ((Double) aggrData.get(minKey) > val) {
					aggrData.put(minKey, val);
				}
				
				String maxKey = alias+".max";
				if ((Double) aggrData.get(maxKey) < val) {
					aggrData.put(maxKey, val);
				}
				
				String countKey = alias+".count";
				Double count = (Double) aggrData.get(countKey) + 1;
				aggrData.put(countKey, count);
				aggrData.put(alias+".avg", sum / count); //TODO Should handle Decimal Formatting
				
				if (isLatest) {
					aggrData.put(alias+".lastValue", val);
				}
			}
		}
	}
	
	private void addFirstTimeAggr (String key, Double val, Map<String, Object> aggrData) {
		aggrData.put(key+".sum", val);
		aggrData.put(key+".min", val);
		aggrData.put(key+".max", val);
		aggrData.put(key+".count", 1d);
		aggrData.put(key+".avg", val);
		aggrData.put(key+".lastValue", val);
	}
	
	private Double getDoubleVal (Object val) {
		if (val != null) {
			if (val instanceof Number) {
				return ((Number) val).doubleValue();
			}
			return new Double(val.toString()); 
		}
		return null;
	}

}
