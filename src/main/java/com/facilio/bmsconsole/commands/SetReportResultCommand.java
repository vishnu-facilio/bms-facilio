package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetReportResultCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String reportType = repContext.getReportType();
		if(reportType.equals(FacilioConstants.Reports.TABULAR_REPORT_TYPE) || reportType.equals(FacilioConstants.Reports.TOP_N_TABULAR_REPORT_TYPE)) {
			return false;
		}
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
 		if(rs!=null && !rs.isEmpty()) {
 	 		JSONArray xAxis = repContext.getXAxis();
 	 		JSONObject xAxisObject = (JSONObject) xAxis.get(0);
 	 		String xAxisFieldAlias = (String) xAxisObject.get(FacilioConstants.Reports.FIELD_ALIAS);  
 	 		if(reportType.equals(FacilioConstants.Reports.NUMERIC_REPORT_TYPE)) {
 	 			Map<String, Object> thisMap = rs.get(0);
 	 			if(thisMap!=null) {
 	 				Object count = thisMap.get(xAxisFieldAlias);
 	 	 			Map<String, Object> thisRecord = new HashMap();
 	 	 			thisRecord.put(FacilioConstants.Reports.VALUE, count);
 	 	 			thisRecord.put(FacilioConstants.Reports.LABEL, xAxisFieldAlias);
 	 	 	 		repContext.put(FacilioConstants.Reports.RESULT_SET,thisRecord);
 	 	 	 		return false;
 	 			}
 	 		}
 	 		JSONArray yAxis = repContext.getYAxis();
 	 		JSONObject yAxisObject = (JSONObject) yAxis.get(0);
 	 		String yAxisFieldAlias = (String) yAxisObject.get(FacilioConstants.Reports.FIELD_ALIAS);
 	 		List<Map<Object, Object>> result = new ArrayList<Map<Object, Object>> ();
 	 		JSONObject categoryObject = null;
 	 		String categoryAlias = "";
 	 		if(yAxis.size()>1) {
 	 			categoryObject = (JSONObject) yAxis.get(1);
 	 			categoryAlias = (String) categoryObject.get(FacilioConstants.Reports.FIELD_ALIAS);
	 	 	 	Map<Object, Object> resultMap = new HashMap();
 	 			for(int i=0;i<rs.size();i++) {
 	 	 			Map<String, Object> thisMap = rs.get(i);
 	 	 			if(thisMap!=null) {
 	 	 				Object label = thisMap.get(xAxisFieldAlias);
 	 	 	 			Object value = thisMap.get(yAxisFieldAlias);
 	 	 	 			Object category = thisMap.get(categoryAlias);
 	 	 	 			Map<Object, Object> thisRecord = (Map<Object, Object>) resultMap.get(label);
 	 	 	 			if(thisRecord==null) {
 	 	 	 				thisRecord = new HashMap();
 	 	 	 				result.add(thisRecord);
 	 	 	 			}
 	 	 	 			thisRecord.put(category, value);
 	 	 	 			thisRecord.put(FacilioConstants.Reports.LABEL, label);
 	 	 	 			resultMap.put(label,thisRecord);
 	 	 			}
 	 	 		}
 	 		}
 	 		else {
 	 	 		for(int i=0;i<rs.size();i++) {
 	 	 			Map<String, Object> thisMap = rs.get(i);
 	 	 			if(thisMap!=null) {
 	 	 				Object label = thisMap.get(xAxisFieldAlias);
 	 	 	 			Object value = thisMap.get(yAxisFieldAlias);
 		 	 	 		Map<Object, Object> thisRecord = new HashMap();
 	 	 	 			thisRecord.put(FacilioConstants.Reports.VALUE, value);
 	 	 	 			thisRecord.put(FacilioConstants.Reports.LABEL, label);
 	 	 	 			result.add(thisRecord);
 	 	 			}
 	 	 		}
 	 		}
 	 		if(!result.isEmpty()) {
 	 			repContext.put(FacilioConstants.Reports.RESULT_SET,result);
 	 		}
 		}
		return false;
	}
}


