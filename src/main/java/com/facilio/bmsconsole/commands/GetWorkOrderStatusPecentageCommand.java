package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetWorkOrderStatusPecentageCommand implements Command{
	
	private static final Logger LOGGER = Logger.getLogger(GetWorkOrderStatusPecentageCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		
		List<Map<String,Object>> tableResp = WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(null, startTime, endTime);
		
		
		//desired resp for chart
		Map<String,Object> graphResp = new HashMap<String, Object>();
		
		List<Object> siteNameObj = new ArrayList<Object>();
		List<Object> overDueObj = new ArrayList<Object>();
		List<Object> onTimeObj = new ArrayList<Object>();
		List<Object> openObj = new ArrayList<Object>();
		
		
		for(int i=0;i<tableResp.size();i++)
		{
			Map<String,Object> site = tableResp.get(i) ;
			siteNameObj.add(site.get("siteName"));
			overDueObj.add(site.get("overdue"));
			onTimeObj.add(site.get("onTime"));
			openObj.add(site.get("open"));
				
			
		}
		
		Map<String,List<Object>> dataResp = new HashMap<String, List<Object>>();
		dataResp.put("x", siteNameObj);
		dataResp.put("ontime", onTimeObj);
		dataResp.put("overdue", overDueObj);
		dataResp.put("open", openObj);
		
		graphResp.put("data",graphResp);
		
		Map<String,Object> axisResp = new HashMap<String, Object>();
		Map<String,Object> xAxisResp = new HashMap<String, Object>();
		Map<String,Object> yAxisResp = new HashMap<String, Object>();
		
		Map<String,Object> xLabelObject = new HashMap<String, Object>();
		xLabelObject.put("text", "Site");
		
		Map<String,Object> yLabelObject = new HashMap<String, Object>();
		yLabelObject.put("text", "Work Order Completion (%) ");
		
		xAxisResp.put("label", xLabelObject);
		xAxisResp.put("datatype","string");
		yAxisResp.put("label", yLabelObject);
			
		axisResp.put("x",xAxisResp);
		axisResp.put("y",yAxisResp);
		axisResp.put("rotated", true);
		
		
		
		axisResp.put("x",xAxisResp);
		axisResp.put("y",yAxisResp);
		
		List<Map<String,Object>> dataPointsResp = new ArrayList<Map<String,Object>>();
		Map<String,Object> onTimeListItem = new HashMap<String, Object>();
		onTimeListItem.put("key","ontime");
		onTimeListItem.put("label","Ontime");
		onTimeListItem.put("color","#39C2B0");
		onTimeListItem.put("type","datapoint");
		
		dataPointsResp.add(onTimeListItem);
	
		Map<String,Object> overDueListItem = new HashMap<String, Object>();
		overDueListItem.put("key","overdue");
		overDueListItem.put("label","Overdue");
		overDueListItem.put("color","#f08532");
		overDueListItem.put("type", "datapoint");
		dataPointsResp.add(overDueListItem);
	
		Map<String,Object> openListItem = new HashMap<String, Object>();
		openListItem.put("key","open");
		openListItem.put("label","Open");
		openListItem.put("color","#fafafa");
		openListItem.put("type","datapoint");
		dataPointsResp.add(openListItem);
	
		Map<String,Object> optionsResp = new HashMap<String, Object>();
		optionsResp.put("axis",axisResp);
		optionsResp.put("dataPoints",dataPointsResp);
		optionsResp.put("type","stackedbar");
		optionsResp.put("widgetLegend",false);
		
		
		dataResp.put("x", siteNameObj);
		dataResp.put("ontime", onTimeObj);
		dataResp.put("overdue", overDueObj);
		dataResp.put("open", openObj);
		
		graphResp.put("data",dataResp);
		graphResp.put("options",optionsResp);
		
		Map<String,Object> outputResp = new HashMap<String, Object>();
		//this is for tabular response
		outputResp.put("tabularData",tableResp);
		
		//this is for graph response
		outputResp.put("graphData",graphResp);
				
		context.put(FacilioConstants.ContextNames.WORK_ORDER_STATUS_PERCENTAGE_RESPONSE, outputResp);
		
		
		return false;
	}
	
	

}

	
	


