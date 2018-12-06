package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;

public class CalculateVarianceCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(CalculateVarianceCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, Map<String, Map<Object, Object>>> reportData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		
		Map<String, Map<String, JSONObject>> varianceData = new HashMap<>();
		
		if(reportData != null && !reportData.isEmpty()) {
			
			for( String assetName : reportData.keySet()) {
				
				 Map<String, Map<Object, Object>> aggregateReportData = reportData.get(assetName);
				
				 Map<String, JSONObject> res = new HashMap<>(); 
				for(String aggregateResult : aggregateReportData.keySet()) {
					
					Map<Object, Object> data = aggregateReportData.get(aggregateResult);
					
					JSONObject variance = getStandardVariance(data);
					
					res.put(aggregateResult, variance);
				}
				
				varianceData.put(assetName, res);
			}
			
		}
		context.put(FacilioConstants.ContextNames.REPORT_VARIANCE_DATA, varianceData);
		
		return false;
	}

	public JSONObject getStandardVariance(Map<Object, Object> data) {
		
		JSONObject variance = new JSONObject();
		Collection<Object> collection = data.values();
		try {
			Double min = null ,max = null,avg = null,sum = (double) 0;
			
			for(Object value1 :collection) {
				if (value1 == null) {
					continue;
				}
				
				Double value = Double.parseDouble(value1.toString());
				sum = sum + value;
				
				if(min == null && max == null) {
					min = value;
					max = value;
				}
				else {
					min = min < value ? min : value;
					max = max > value ? max : value;
				}
			}
			if(sum > 0 && collection.size() > 0) {
				avg = sum / collection.size();
			}
			
			List keys = new ArrayList<>(data.keySet());
			if(keys != null && !keys.isEmpty()) {
				Collections.sort(keys);
				variance.put("lastValue", data.get(keys.get(keys.size()-1)));
			}
			
			variance.put("min", FacilioUtil.decimalClientFormat(min));
			variance.put("max", FacilioUtil.decimalClientFormat(max));
			variance.put("avg", FacilioUtil.decimalClientFormat(avg));
			variance.put("sum", FacilioUtil.decimalClientFormat(sum));
			
		}
		catch(Exception e) {
			
		}
		return variance;
	}
}
