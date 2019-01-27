package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.actions.V2ReportAction;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseLineContext.AdjustType;
import com.facilio.bmsconsole.context.BaseLineContext.RangeType;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ReportSpaceFilterContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.cards.util.CardType;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.util.WorkflowUtil;

public class FetchCardDataCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(FetchCardDataCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		
		WidgetStaticContext widgetStaticContext = (WidgetStaticContext) context.get(FacilioConstants.ContextNames.WIDGET_STATIC_CONTEXT);
		ReportSpaceFilterContext reportSpaceFilterContext = (ReportSpaceFilterContext) context.get(FacilioConstants.ContextNames.REPORT_SPACE_FILTER_CONTEXT);
		
		Map<String,Object> result = null;
		if(widgetStaticContext != null) {
			
			widgetStaticContext.setParamsJson(DashboardUtil.getCardParams(widgetStaticContext.getParamsJson()));
			if(CardUtil.isGetDataFromEnum(widgetStaticContext.getStaticKey())) {
				
				result = new HashMap<>();
				
				CardType card = CardType.getCardType(widgetStaticContext.getStaticKey());
				
				if(DashboardUtil.isDynamicWFGeneratingCard(widgetStaticContext.getStaticKey())) {
					card.setWorkflow(widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflowString()); // check in pd7 org
				}
				
				if(widgetStaticContext.getStaticKey().equals(CardType.FAHU_STATUS_CARD_NEW.getName())) {
					CardUtil.fillParamJsonForFahuCard(AccountUtil.getCurrentOrg().getId(),widgetStaticContext.getParamsJson());
				}
				
				if(card.isSingleResultWorkFlow()) {
					Object wfResult = WorkflowUtil.getWorkflowExpressionResult(card.getWorkflow(), widgetStaticContext.getParamsJson());
					
					wfResult = CardUtil.getWorkflowResultForClient(wfResult, widgetStaticContext); // parsing data suitable for client
					result.put("result", wfResult);
					
					result.put("unit", CardUtil.getUnit(widgetStaticContext.getParamsJson()));
				}
				else {
					Map<String, Object> expResult = WorkflowUtil.getExpressionResultMap(card.getWorkflow(), widgetStaticContext.getParamsJson());
					
					expResult = (Map<String, Object>) CardUtil.getWorkflowResultForClient(expResult, widgetStaticContext); // parsing data suitable for client
					
					result.put("result", expResult);
				}
				result.put("widget", widgetStaticContext);
			}
			
			else if(CardUtil.isExtraCard(widgetStaticContext.getStaticKey())) {
				
				result = new HashMap<>();
				
				if(widgetStaticContext.getStaticKey().equals("readingWithGraphCard")) {
					
					V2ReportAction reportAction = new V2ReportAction();
					if(widgetStaticContext.getId() > 0) {
						reportAction.setCardWidgetId(widgetStaticContext.getId() );
					}
					else {
						reportAction.setCardParamJson(widgetStaticContext.getParamsJson());
					}
					reportAction.fetchReadingsFromCard();
					
					FacilioContext facilioContext = reportAction.getResultContext();
					
					result.put("result", facilioContext);
					
					JSONObject params = widgetStaticContext.getParamsJson();
					
					 Map<Long, ReadingRuleAlarmMeta> alarmMeta = ReadingRuleAPI.fetchAlarmMeta((Long)params.get("parentId"), (Long)params.get("fieldId"));
					 
					List<AlarmContext> alarms = AlarmAPI.getAlarms(alarmMeta.keySet());
					
					result.put("alarmSeverity", AlarmAPI.getMaxSeverity(alarms));
					
					result.put("unit", CardUtil.getUnit(params));
					
					result.put("widget", widgetStaticContext);
				}
			}
			if(widgetStaticContext.getWidgetVsWorkflowContexts() != null) {
				
				result = new HashMap<>();
				
				for(WidgetVsWorkflowContext widgetVsWorkflowContext : widgetStaticContext.getWidgetVsWorkflowContexts()) {
					
					if(widgetStaticContext.getStaticKey().equals("profilemini") && widgetVsWorkflowContext.getBaseSpaceId() != null) {
						
						BuildingContext building = SpaceAPI.getBuildingSpace(widgetVsWorkflowContext.getBaseSpaceId());
						
						List<EnergyMeterContext> meters = DeviceAPI.getMainEnergyMeter(building.getId()+"");
						
						EnergyMeterContext meter = meters.get(0);
						
						DateOperators dateOpp = DateOperators.CURRENT_MONTH;
						
						BaseLineContext baseline = BaseLineAPI.getBaseLine(RangeType.PREVIOUS_MONTH);
						DateRange lastMonthUptoNow = baseline.calculateBaseLineRange(new DateRange(dateOpp.getRange(null).getStartTime(), DateTimeUtil.getCurrenTime()), AdjustType.NONE);
						
						double previousValue = DashboardAction.getTotalKwh(Collections.singletonList(meter.getId()+""), lastMonthUptoNow.getStartTime(), lastMonthUptoNow.getEndTime());
						
						Double value = DashboardAction.getTotalKwh(Collections.singletonList(meter.getId()+""), dateOpp.getRange(null).getStartTime(), dateOpp.getRange(null).getEndTime());
						JSONObject json1 = new JSONObject();
						
						json1.put("consumption", value);
						json1.put("unit", "kWh");
						
						JSONObject json = new JSONObject();
						
						json.put("name", building.getName());
						
						if(building.getPhotoId() <= 0) {
							
							List<PhotosContext> photos = SpaceAPI.getBaseSpacePhotos(building.getId());
							
							if(photos != null && !photos.isEmpty()) {
								building.setPhotoId(photos.get(0).getPhotoId());
							}
						}
						
						json.put("avatar", building.getAvatarUrl());
						json.put("currentVal", json1);
						
						json.put("variance", ReportsUtil.getVariance(value, previousValue));
						
						result.put("card", json);
						result.put("building", building);
					}
					else {
						try {
							Map<String,Object> paramMap = null;
							if(widgetVsWorkflowContext.getBaseSpaceId() != null) {
								if(paramMap == null) {
									paramMap = new HashMap<>();
								}
								paramMap.put("parentId", widgetVsWorkflowContext.getBaseSpaceId());
								
								if(widgetStaticContext != null && ( ((widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI)) && widgetVsWorkflowContext.getWorkflowName().equals("weather"))  || (widgetStaticContext.getStaticKey().equals("weathercardaltayer") && widgetVsWorkflowContext.getWorkflowName().equals("weather")) )) {
									BaseSpaceContext basespace = SpaceAPI.getBaseSpace(widgetVsWorkflowContext.getBaseSpaceId());
									if(basespace != null) {
										paramMap.put("parentId", basespace.getSiteId());
									}
								}
							}
							if(reportSpaceFilterContext != null) {
								if(paramMap == null) {
									paramMap = new HashMap<>();
								}
								if(reportSpaceFilterContext.getBuildingId() != null) {
									paramMap.put("parentId", reportSpaceFilterContext.getBuildingId());
								}
								if(widgetStaticContext != null && ( ((widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD) || widgetStaticContext.getStaticKey().equals(DashboardUtil.STATIC_WIDGET_WEATHER_CARD_MINI)) && widgetVsWorkflowContext.getWorkflowName().equals("weather"))  || (widgetStaticContext.getStaticKey().equals("weathercardaltayer") && widgetVsWorkflowContext.getWorkflowName().equals("weather")) )) {
									BaseSpaceContext basespace = SpaceAPI.getBaseSpace(reportSpaceFilterContext.getBuildingId());
									if(basespace != null) {
										paramMap.put("parentId", basespace.getSiteId());
									}
								}
							}
							if (widgetVsWorkflowContext.getWorkflowName().equals("lastMonthThisDate") || widgetVsWorkflowContext.getWorkflowName().equals("lastMonthDate")){
								if(paramMap == null) {
									paramMap = new HashMap<>();
								}
								DateRange range1 = DateOperators.CURRENT_MONTH_UPTO_NOW.getRange(null);
								paramMap.put("startTime", range1.getStartTime());
								paramMap.put("endTime", range1.getEndTime());
							}
							Object wfResult = null;
							if(widgetVsWorkflowContext.getWorkflowId() != null) {
								wfResult = WorkflowUtil.getResult(widgetVsWorkflowContext.getWorkflowId(), paramMap);
							}
							else {
								wfResult = WorkflowUtil.getWorkflowExpressionResult(widgetVsWorkflowContext.getWorkflowString(), paramMap);
							}
							
							if(widgetStaticContext != null && (widgetStaticContext.getStaticKey().equals("weathercard") || widgetStaticContext.getStaticKey().equals("weathermini")) && widgetVsWorkflowContext.getWorkflowName().equals("weather")) {
								Map<String,Object> ss = (Map<String, Object>) wfResult;
								Object temprature = ss.get("temperature");
								if(AccountUtil.getCurrentOrg().getOrgId() == 104l || AccountUtil.getCurrentOrg().getOrgId() == 75l) {
									ss.put("unit", "F");
								}
								DecimalFormat f = new DecimalFormat("##.0");
								ss.put("temperature", f.format(temprature));
								
							}
							
							LOGGER.severe("widgetVsWorkflowContext.getWorkflowId() --- "+widgetVsWorkflowContext.getWorkflowId() +" wfResult --  "+wfResult);
							result.put(widgetVsWorkflowContext.getWorkflowName(), wfResult);
						}
						catch(Exception e) {
							LOGGER.severe(e.getMessage());
						}
					}
				}
			} 
			LOGGER.severe("result --- "+result);
			context.put(FacilioConstants.ContextNames.RESULT, result);
		}
		return false;
	}

}
