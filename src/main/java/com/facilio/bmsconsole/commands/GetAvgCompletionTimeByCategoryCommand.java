package com.facilio.bmsconsole.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAvgCompletionTimeByCategoryCommand implements Command {

	
	private static final Logger LOGGER = Logger.getLogger(GetAvgCompletionTimeByCategoryCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
			LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
			
			long startTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_ENDTIME);
			
			List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getAvgCompletionTimeByCategory(startTime, endTime);
			
			
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
				List<Long> allSites = getAllSiteList();
				
				Map<Long, Object> sitesMap = new HashMap<Long, Object>();

				if (resp.containsKey(categoryId)) {
					sitesMap = (Map<Long, Object>) resp.get(categoryId).get("sitesMap");
				}

				else {
					
					sitesMap = new HashMap<Long, Object>();
					
					for(int j=0;j<allSites.size();j++)
					{
						Long id = allSites.get(j);
						Map<String,Object> eachSite = new HashMap<String, Object>();
						eachSite.put("siteId", id);
						eachSite.put("siteName", siteArray.get(id));
						eachSite.put("avgResolutionTime", 0);
						eachSite.put("count", 0);
						
						sitesMap.put(allSites.get(j),eachSite);
					}
					Map<String,Object> categoryInfo = new HashMap<String, Object>();
						categoryInfo.put("categoryId", categoryId);
						categoryInfo.put("categoryName", categoryName);
						categoryInfo.put("sitesMap", sitesMap);
						resp.put(categoryId,categoryInfo);
					
				}
				Map<String, Object> group = (Map<String, Object>)sitesMap.get(siteId);
				
				group.put("siteId", siteId);
				group.put("siteName", siteName);
				avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;
				group.put("avgResolutionTime", avgResolutionTime);
				group.put("count", count);
				
				sitesMap.put(siteId,group);
				
				
			}
			
			List<Map<String,Object>> tableResp = new ArrayList<Map<String,Object>>(resp.values());
			
			for(int i=0;i<tableResp.size();i++)
			{
				Map<String,Object> eachCategory = tableResp.get(i);
				Map<Long,Map<String, Object>> sitesMap = (Map<Long,Map<String, Object>>)eachCategory.get("sitesMap");
				List<Map<String,Object>> siteResp = new ArrayList<Map<String,Object>>(sitesMap.values());
				eachCategory.put("resolutionTimeGrouping",siteResp );
				eachCategory.remove("sitesMap");
				
			}
			
					
			//desired resp for chart
			List<Map<String,Object>> graphResp = new ArrayList<Map<String,Object>>();
					
			
			for(int i=0;i<tableResp.size();i++)
			{
				Map<String,Object> category = tableResp.get(i) ;
				List<Object> siteNameObj = new ArrayList<Object>();
				List<Object> resolutionTimeObj = new ArrayList<Object>();
						
				Map<String,Object> perCategoryGraphResp = new HashMap<String, Object>();
				Map<String,List<Object>> dataResp = new HashMap<String, List<Object>>();
				
				List<Map<String,Object>> siteGroup = (List<Map<String, Object>>) category.get("resolutionTimeGrouping");
				for(int j=0;j<siteGroup.size();j++)
				{
					Map<String,Object> siteObj = siteGroup.get(j);	
					siteNameObj.add(siteObj.get("siteName"));
					resolutionTimeObj.add(siteObj.get("avgResolutionTime"));
				}
				
				dataResp.put("x", siteNameObj);
				dataResp.put("y", resolutionTimeObj);
				
				
				Map<String,Object> axisResp = new HashMap<String, Object>();
				Map<String,Object> xAxisResp = new HashMap<String, Object>();
				Map<String,Object> yAxisResp = new HashMap<String, Object>();
				
				Map<String,Object> xLabelObject = new HashMap<String, Object>();
				xLabelObject.put("text", "Site");
				
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
				avgResolutionTimeItem.put("label","ResolutionTime");
				avgResolutionTimeItem.put("color","#39C2B0");
				avgResolutionTimeItem.put("type","datapoint");
				
				dataPointsResp.add(avgResolutionTimeItem);
			
				Map<String,Object> optionsResp = new HashMap<String, Object>();
				optionsResp.put("axis",axisResp);
				optionsResp.put("type","bar");
				optionsResp.put("dataPoints",dataPointsResp);
				optionsResp.put("widgetLegend",false);
				
				
				perCategoryGraphResp.put("data",dataResp);
				perCategoryGraphResp.put("options",optionsResp);
				perCategoryGraphResp.put("categoryId",category.get("categoryId"));
				perCategoryGraphResp.put("categoryName",category.get("categoryName"));
				
				graphResp.add(perCategoryGraphResp);
				
			}
			
			Map<String,Object> outputResp = new HashMap<String, Object>();
			//this is for tabular response
			outputResp.put("tabularData",tableResp);
			
			//this is for graph response
			outputResp.put("graphData",graphResp);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_AVG_RESOLUTION_TIME, outputResp);
			

		
		return false;
	}
	
	private List<Long> getAllSiteList() throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule sitesModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> siteFields = modBean.getAllFields(sitesModule.getName());
		

		SelectRecordsBuilder<SiteContext> selectRecordsBuilder = new SelectRecordsBuilder<SiteContext>()
				  													  .module(sitesModule)
				  													  .beanClass(SiteContext.class)
				  													  .select(siteFields)
				  													  ;



		Map<Long, SiteContext> sitesMap  = selectRecordsBuilder.getAsMap();
		List<Long> siteIds = new ArrayList<Long>(sitesMap.keySet());
				
		return siteIds;
	        
      


	}
	
}


