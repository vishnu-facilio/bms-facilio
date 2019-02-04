package com.facilio.workflows.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketStatusContext.StatusType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioWorkOrderFunctions implements FacilioWorkflowFunctionInterface {

	GET_WORKORDERS_COMPLETION_TIME(1,"getWorkOrdersByCompletionTime") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
				List<WorkOrderContext> workOrderStatusCount = WorkOrderAPI.getWorkOrderStatusPercentage(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()));
			    TicketAPI.loadTicketLookups(workOrderStatusCount);
			    
			    return GET_WORKORDERS_COMPLETION_TIME.constructWOByCompletionResponse(workOrderStatusCount);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	GET_AVG_RESOLUTION_TIME(2,"getAvgResolutionTime") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getAvgCompletionTimeByCategory(Long.valueOf(objects[0].toString()),Long.valueOf(objects[1].toString()));
			
            return GET_AVG_RESOLUTION_TIME.constructAvgTimeResponse(avgResolutionTimeByCategory);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "workorder";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.WORKORDER;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioWorkOrderFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioWorkOrderFunctions> getAllFunctions() {
		return WORKORDER_FUNCTIONS;
	}
	public static FacilioWorkOrderFunctions getFacilioWorkOrderFunction(String functionName) {
		return WORKORDER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioWorkOrderFunctions> WORKORDER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioWorkOrderFunctions> initTypeMap() {
		Map<String, FacilioWorkOrderFunctions> typeMap = new HashMap<>();
		for(FacilioWorkOrderFunctions type : FacilioWorkOrderFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
	
	private List<Map<String,Object>> constructWOByCompletionResponse(List<WorkOrderContext> workOrderStatusCount) throws Exception{
	
		Map<Long, Object> siteArray = WorkOrderAPI.getLookupFieldPrimary("site");
		
		Map<Long, Map<String, Object>> resp = new HashMap<Long, Map<String, Object>>();
		for (int i = 0; i < workOrderStatusCount.size(); i++) {
			WorkOrderContext mp = workOrderStatusCount.get(i);
			String siteName = (String) siteArray.get(mp.getSiteId());
			TicketStatusContext statusMap = mp.getStatus();
			Long actualWorkDuration = mp.getActualWorkDuration();
			Long estimatedWorkDuration = mp.getEstimatedWorkDuration();
			Long siteId = mp.getSiteId();
			Long dueDate = mp.getDueDate();
			Long actualWorkEnd = mp.getActualWorkEnd();
			
			
			Map<String,Object> siteInfo = null;
			
			boolean overDue = false;
			boolean onTime = false;
			boolean open = false;
		    if(statusMap.getType() == StatusType.CLOSED)
            {
            	if(dueDate != null && actualWorkEnd != null) {
            	if(actualWorkEnd <= dueDate)
            	{
            		onTime = true;
            	}
            	else
            	{
            		overDue = true;
            	}
            	}
            }
            else
            {
            	open = true;
            }
		
			if (resp.containsKey(siteId)) {
                siteInfo = resp.get(siteId);
               if(overDue)
               {
               Integer overDueCount = (Integer)siteInfo.get("overDue");
               overDueCount = overDueCount + 1;
               siteInfo.put("overDue",overDueCount);
               }
               else if(onTime)
               {
            	   Integer onTimeCount = (Integer)siteInfo.get("onTime");
                   onTimeCount = onTimeCount + 1;
                   siteInfo.put("onTime",onTimeCount);
                 
               }
               else if(open)
               {
            	   Integer openCount = (Integer)siteInfo.get("open");
            	   openCount = openCount + 1;
                   siteInfo.put("open",openCount);
                 
               }
               Integer totalCount = (Integer)siteInfo.get("totalCount");
          	   siteInfo.put("totalCount",totalCount+1);  
     		}

			else {
				
				siteInfo = new HashMap<String, Object>();
				siteInfo.put("open",open?1:0);
				siteInfo.put("name",siteName);
				siteInfo.put("overDue",overDue?1:0);
				siteInfo.put("onTime",onTime?1:0);
				siteInfo.put("siteId",siteId);
				siteInfo.put("totalCount",1);
				
			}
			  resp.put(siteId, siteInfo);
			
	      	
		
		}
		
		List<Map<String,Object>> tableResp = new ArrayList<Map<String,Object>>(resp.values());
		return tableResp;
		
	}
	
	private List<Map<String,Object>> constructAvgTimeResponse(List<Map<String,Object>> avgResolutionTimeByCategory) throws Exception {
	
		Map<Long, Object> ticketCategoryArray = WorkOrderAPI.getLookupFieldPrimary("ticketcategory");

		 Map<Long, Object> siteArray = WorkOrderAPI.getLookupFieldPrimary("site");

	
		 Map<Long, Map<String, Object>> resp = new HashMap<Long, Map<String, Object>>();
		for (int i = 0; i < avgResolutionTimeByCategory.size(); i++) {
            Map<String,Object> mp = avgResolutionTimeByCategory.get(i);
			Double avgResolutionTime = ((BigDecimal)mp.get("avg_resolution_time")).doubleValue();
			Long count = (Long) mp.get("count");
			Long categoryId = (Long) mp.get("category");
			String categoryName = (String) ticketCategoryArray.get(categoryId);
			
			Long siteId = (Long) mp.get("siteId");
			String siteName = (String) siteArray.get(siteId);
			Map<String, Object> group = new HashMap<String, Object>();

			List<Map<String,Object>> resolutionTimeGrouping = null;
			if (resp.containsKey(categoryId)) {
				resolutionTimeGrouping =(List<Map<String,Object>>)resp.get(categoryId).get("resolutionTimeGrouping");
				
			}

			else {
				
				resolutionTimeGrouping = new ArrayList<Map<String,Object>>();
				
				
				Map<String,Object> categoryInfo = new HashMap<String, Object>();
				categoryInfo.put("categoryId", categoryId);
				categoryInfo.put("categoryName", categoryName);
				categoryInfo.put("resolutionTimeGrouping", resolutionTimeGrouping);
				resp.put(categoryId,categoryInfo);

				
			}
			
			group.put("siteId", siteId);
			group.put("siteName", siteName);
			avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;
			group.put("avgResolutionTime", avgResolutionTime);
			group.put("count", count);
			
			resolutionTimeGrouping.add(group);
			
			
			
		}
		
		List<Map<String,Object>> tableResp = new ArrayList<Map<String,Object>>(resp.values());
		
		for(int i=0;i<tableResp.size();i++)
		{
			Map<String,Object> eachCategory = tableResp.get(i);
			List<Map<String, Object>> sitesMap = (List<Map<String, Object>>)eachCategory.get("resolutionTimeGrouping");
			Double total = 0.0;
			for(int k=0;k<sitesMap.size();k++)
			{
				total+=(Double)sitesMap.get(k).get("avgResolutionTime");
			}
			eachCategory.put("siteCount",sitesMap.size());
			eachCategory.put("totalAvg",total);
			
			eachCategory.remove("resolutionTimeGrouping");
		}
		return tableResp;

	}
	
		
}
