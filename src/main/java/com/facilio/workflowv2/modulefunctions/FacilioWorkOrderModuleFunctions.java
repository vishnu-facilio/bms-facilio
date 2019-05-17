package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.WorkOrderAPI;

public class FacilioWorkOrderModuleFunctions extends FacilioModuleFunctionImpl {

	public List<Map<String,Object>> getAvgResolutionTime(List<Object> objects) throws Exception {
		return WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects.get(0).toString()),Long.valueOf(objects.get(1).toString()),Long.valueOf(objects.get(2).toString()));
	}
	
	public List<Map<String,Object>> getWorkOrdersByCompletionTime(List<Object> objects) throws Exception {
		return WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects.get(0)),Long.valueOf(objects.get(1).toString()),Long.valueOf(objects.get(2).toString()));
	}
	
	public List<Map<String, Object>> getTopNTechnicians(List<Object> objects) throws Exception{
		return WorkOrderAPI.getTopNTechnicians(objects.get(0).toString(), Long.valueOf(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()));
	}
}
