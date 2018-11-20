package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;

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
		if (reportData != null && !reportData.isEmpty()) {
			List<Map<String, Object>> csvData = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			Map<String, Double> reportAggrData = (Map<String, Double>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
			
			Map<String, Double> aggrData = new HashMap<>();
			for (Map<String, Object> data : csvData) {
				boolean isLatest = false;
				
				if (sortAlias != null && !sortAlias.isEmpty()) {
					Double sortVal = getDoubleVal(data.get(sortAlias));
					if (sortVal != null) {
						Double currentSortVal = aggrData.get(sortAlias+".lastValue");
						isLatest = currentSortVal == null || currentSortVal <= sortVal;
					}
				}
				
				for (Map.Entry<String, Object> entry : data.entrySet()) {
					Double val = getDoubleVal(entry.getValue());
					if (val != null) {
						String sumKey = entry.getKey()+".sum";
						if (reportAggrData == null || reportAggrData.get(sumKey) == null) {
							Double sum = aggrData.get(sumKey); 
							if (sum == null) {
								addFirstTimeAggr(entry.getKey(), val, aggrData);
							}
							else {
								sum = sum + val; 
								aggrData.put(sumKey, sum);
								
								String minKey = entry.getKey()+".min";
								if (aggrData.get(minKey) > val) {
									aggrData.put(minKey, val);
								}
								
								String maxKey = entry.getKey()+".max";
								if (aggrData.get(maxKey) < val) {
									aggrData.put(maxKey, val);
								}
								
								String countKey = entry.getKey()+".count";
								Double count = aggrData.get(countKey) + 1;
								aggrData.put(countKey, count);
								aggrData.put(entry.getKey()+".avg", sum / count); //TODO Should handle Decimal Formatting
								
								if (isLatest) {
									aggrData.put(entry.getKey()+".lastValue", val);
								}
							}
						}
					}
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
	
	private void addFirstTimeAggr (String key, Double val, Map<String, Double> aggrData) {
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
			else if (NumberUtils.isCreatable(val.toString())) {
				return new Double(val.toString()); 
			}
		}
		return null;
	}

}
