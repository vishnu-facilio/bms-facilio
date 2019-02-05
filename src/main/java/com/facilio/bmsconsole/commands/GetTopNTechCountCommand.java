package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetTopNTechCountCommand implements Command{

	private static final Logger LOGGER = Logger.getLogger(GetTopNTechCountCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());
		long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
		String count = (String) context.get(FacilioConstants.ContextNames.WORK_ORDER_TECHNICIAN_COUNT);
		
		List<Map<String, Object>> countMap = WorkOrderAPI.getTopNTechnicians(count, startTime, endTime);
		
				
		//desired resp for chart
				
		List<Object> techNameObj = new ArrayList<Object>();
		List<Object> closedWoCountObj = new ArrayList<Object>();
		Map<String,Object> axisResp = new HashMap<String, Object>();
		Map<String,Object> xAxisResp = new HashMap<String, Object>();
		Map<String,Object> yAxisResp = new HashMap<String, Object>();
		Map<String,List<Object>> dataResp = new HashMap<String, List<Object>>();
		Map<String,Object> techGraphResp = new HashMap<String, Object>();
		
		
		Map<String,Object> xLabelObject = new HashMap<String, Object>();
		xLabelObject.put("text", "Technician");
		
		Map<String,Object> yLabelObject = new HashMap<String, Object>();
		yLabelObject.put("text", "Closed WorkOrder Count");
		
		xAxisResp.put("label", xLabelObject);
		xAxisResp.put("datatype","string");
		yAxisResp.put("label", yLabelObject);
			
		axisResp.put("x",xAxisResp);
		axisResp.put("y",yAxisResp);
		
		List<Map<String,Object>> dataPointsResp = new ArrayList<Map<String,Object>>();
		Map<String,Object> workOrderCountItem = new HashMap<String, Object>();
		workOrderCountItem.put("key","closedWorkOrderCount");
		workOrderCountItem.put("label","ClosedWorkOrderCount");
		workOrderCountItem.put("color","#6A74F9");
		workOrderCountItem.put("type","datapoint");
		
		dataPointsResp.add(workOrderCountItem);
	
		Map<String,Object> optionsResp = new HashMap<String, Object>();
		optionsResp.put("axis",axisResp);
		optionsResp.put("type","bar");
		optionsResp.put("dataPoints",dataPointsResp);
		optionsResp.put("widgetLegend",false);
		
		for(int i=0;i<countMap.size();i++)
		{
			Map<String,Object> techInfo = countMap.get(i) ;
			String xName = techInfo.get("user_name")+" "+techInfo.get("site_name");
			techNameObj.add(xName);
			closedWoCountObj.add(techInfo.get("count"));
			
		}
		dataResp.put("x", techNameObj);
		dataResp.put("y", closedWoCountObj);
	
		techGraphResp.put("data",dataResp);
		techGraphResp.put("options",optionsResp);
		
	
		
		Map<String,Object> outputResp = new HashMap<String, Object>();
		//this is for tabular response
		outputResp.put("tabularData",countMap);
		
		//this is for graph response
		outputResp.put("graphData",techGraphResp);
		
		context.put(FacilioConstants.ContextNames.TOP_N_TECHNICIAN, outputResp);
	
		return false;
	}


	
}
