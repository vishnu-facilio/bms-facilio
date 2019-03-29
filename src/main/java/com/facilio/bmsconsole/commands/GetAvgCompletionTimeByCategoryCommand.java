package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetAvgCompletionTimeByCategoryCommand implements Command {

	
	private static final Logger LOGGER = Logger.getLogger(GetAvgCompletionTimeByCategoryCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
			LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
			long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
			long siteId = 0;
			if(context.get(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID)!=null) {
			 siteId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID);
			}
			
			List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getAvgCompletionTimeByCategory(startTime, endTime,siteId);
			
			
			Map<Long, Object> ticketCategoryArray = WorkOrderAPI.getLookupFieldPrimary("ticketcategory");

			List<Object> categoryNameObj = new ArrayList<Object>();
			List<Object> resolutionTimeObj = new ArrayList<Object>();
		
			 Map<Long, Map<String, Object>> resp = new HashMap<Long, Map<String, Object>>();
			for (int i = 0; i < avgResolutionTimeByCategory.size(); i++) {
                 Map<String,Object> mp = avgResolutionTimeByCategory.get(i);
				Double avgResolutionTime = ((BigDecimal)mp.get("avg_resolution_time")).doubleValue();
				Long count = (Long) mp.get("count");
				Long categoryId = (Long) mp.get("category");
				String categoryName = (String) ticketCategoryArray.get(categoryId);
				Map<String,Object> categoryInfo = new HashMap<String, Object>();
				categoryInfo.put("categoryId", categoryId);
				categoryInfo.put("categoryName", categoryName);
				avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;
				categoryInfo.put("avgResolutionTime",avgResolutionTime );
				resp.put(categoryId,categoryInfo);
				
				categoryNameObj.add(categoryName);
				resolutionTimeObj.add(avgResolutionTime);
		
				
			}
			
			List<Map<String,Object>> tableResp = new ArrayList<Map<String,Object>>(resp.values());
			
			//desired resp for chart
			
			Map<String,Object> axisResp = new HashMap<String, Object>();
			Map<String,Object> xAxisResp = new HashMap<String, Object>();
			Map<String,Object> yAxisResp = new HashMap<String, Object>();
			Map<String,List<Object>> dataResp = new HashMap<String, List<Object>>();
			Map<String,Object> categoryGraphResp = new HashMap<String, Object>();
			
			
			Map<String,Object> xLabelObject = new HashMap<String, Object>();
			xLabelObject.put("text", "Category");
			
			Map<String,Object> yLabelObject = new HashMap<String, Object>();
			yLabelObject.put("text", "Avg Resolution Time");
			
			xAxisResp.put("label", xLabelObject);
			xAxisResp.put("datatype","string");
			yAxisResp.put("label", yLabelObject);
				
			axisResp.put("x",xAxisResp);
			axisResp.put("y",yAxisResp);
			
			List<Map<String,Object>> dataPointsResp = new ArrayList<Map<String,Object>>();
			Map<String,Object> avgResolutionTimeItem = new HashMap<String, Object>();
			avgResolutionTimeItem.put("key","avgResolutionTime");
			avgResolutionTimeItem.put("label","AvgResolutionTime");
			avgResolutionTimeItem.put("color","#CF77DB");
			avgResolutionTimeItem.put("type","datapoint");
			
			dataPointsResp.add(avgResolutionTimeItem);
		
			Map<String,Object> optionsResp = new HashMap<String, Object>();
			optionsResp.put("axis",axisResp);
			optionsResp.put("type","bar");
			optionsResp.put("dataPoints",dataPointsResp);
			optionsResp.put("widgetLegend",false);
			
		
			dataResp.put("x", categoryNameObj);
			dataResp.put("y", resolutionTimeObj);
		
			categoryGraphResp.put("data",dataResp);
			categoryGraphResp.put("options",optionsResp);
			
		
			
			Map<String,Object> outputResp = new HashMap<String, Object>();
			//this is for tabular response
			outputResp.put("tabularData",tableResp);
			
			//this is for graph response
			outputResp.put("graphData",categoryGraphResp);
			
		
			
		context.put(FacilioConstants.ContextNames.WORK_ORDER_AVG_RESOLUTION_TIME, outputResp);
			

		
		return false;
	}
	
		
}


