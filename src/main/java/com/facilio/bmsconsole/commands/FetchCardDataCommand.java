package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.actions.V2ReportAction;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReportSpaceFilterContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.context.WidgetVsWorkflowContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleAlarmMeta;
import com.facilio.cards.util.CardType;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.events.constants.EventConstants.EventFieldFactory;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.modules.BaseLineContext.RangeType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class FetchCardDataCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(FetchCardDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		WidgetStaticContext widgetStaticContext = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		// param after save
		Long widgetId = (Long) context.get(FacilioConstants.ContextNames.WIDGET_ID);
		
		// param before save
		String staticKey = (String) context.get(FacilioConstants.ContextNames.WIDGET_STATIC_KEY);
		Long baseSpaceId = (Long) context.get(FacilioConstants.ContextNames.WIDGET_BASESPACE_ID);
		WorkflowContext workflow = (WorkflowContext) context.get(FacilioConstants.ContextNames.WIDGET_WORKFLOW);
		JSONObject paramsJson = (JSONObject) context.get(FacilioConstants.ContextNames.WIDGET_PARAMJSON);
		Boolean isRca = (Boolean) context.get(FacilioConstants.ContextNames.IS_RCA);
		ReportSpaceFilterContext reportSpaceFilterContext = (ReportSpaceFilterContext)  context.get(FacilioConstants.ContextNames.WIDGET_REPORT_SPACE_FILTER_CONTEXT);
		
		Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
		Long endTime = (Long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		if(widgetId != null) {
			DashboardWidgetContext dashboardWidgetContext =  DashboardUtil.getWidget(widgetId);
			widgetStaticContext = (WidgetStaticContext) dashboardWidgetContext;
		}
		else {
			widgetStaticContext = new WidgetStaticContext();
			widgetStaticContext.setStaticKey(staticKey);
			widgetStaticContext.setBaseSpaceId(baseSpaceId);
			
			if(workflow != null) {
				WidgetVsWorkflowContext widgetVsWorkflowContext = new WidgetVsWorkflowContext();
				widgetVsWorkflowContext.setWorkflow(workflow);
				widgetStaticContext.addWidgetVsWorkflowContexts(widgetVsWorkflowContext);
			}
			else {
				List<WidgetVsWorkflowContext> workflowList = DashboardUtil.getCardWorkflowBasedOnStaticKey(staticKey);
				if(workflowList != null) {
					for(WidgetVsWorkflowContext workflow1 :workflowList) {
						workflow1.setBaseSpaceId(baseSpaceId);
					}
				}
				widgetStaticContext.setWidgetVsWorkflowContexts(workflowList);
			}
		}
		if(paramsJson != null) {
			widgetStaticContext.setParamsJson(paramsJson);
		}
		
		if(widgetStaticContext != null) {
			
			Map<String,Object> result = null;
			widgetStaticContext.setParamsJson(DashboardUtil.getCardParams(widgetStaticContext.getParamsJson()));
			if(CardUtil.isGetDataFromEnum(widgetStaticContext.getStaticKey())) {
				
				result = new HashMap<>();
				
				Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
				
				if(startTime != null && endTime != null && startTime > 0 && endTime > 0) {
//					if(widgetStaticContext.getParamsJson().get("moduleName") != null) {
//						FacilioModule module =  modBean.getModule((String) widgetStaticContext.getParamsJson().get("moduleName"));
//						boolean isReadingModule = FacilioUtil.isReadingModule(module);
//						
//						if(isReadingModule) {
//							
//							Condition dateCondition = CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN);
//							if(criteria == null) {
//								criteria = new Criteria();
//							}
//							criteria.addAndCondition(dateCondition);
//						}
//					}
					if(widgetStaticContext.getParamsJson() != null) {
						widgetStaticContext.getParamsJson().put("startTime", startTime);
						widgetStaticContext.getParamsJson().put("endTime", endTime);
					}
				}
				
				CardType card = CardType.getCardType(widgetStaticContext.getStaticKey());
				
				boolean isNewWorkflowCard = false;
				
				String workflowString = card.getWorkflow();
				if(card.isDynamicWfGeneratingCard()) {
					workflowString = widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflowString();
					if(widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflowId() != null) {
						WorkflowContext workflowTemp = WorkflowUtil.getWorkflowContext(widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflowId());
						if(workflowTemp.isV2Script()) {
							workflowString = workflowTemp.getWorkflowV2String();
							isNewWorkflowCard = true;
						}
					}
					else if(widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflow() != null && widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflow().isV2Script()) {
						isNewWorkflowCard = true;
						workflowString = widgetStaticContext.getWidgetVsWorkflowContexts().get(0).getWorkflow().getWorkflowV2String();
					}
				}
				
				if(widgetStaticContext.getId() == 7849 ||widgetStaticContext.getId() == 7848 ||widgetStaticContext.getId() ==7847 ||widgetStaticContext.getId() ==7846) {
					
					LOGGER.log(Level.SEVERE, "2.widgetStaticContext.getId() -- "+widgetStaticContext.getId()+" workflow == " +workflowString);
				}
				
				if(widgetStaticContext.getStaticKey().equals(CardType.FAHU_STATUS_CARD_NEW.getName())) {
					CardUtil.fillParamJsonForFahuCard(AccountUtil.getCurrentOrg().getId(),widgetStaticContext.getParamsJson());
				}
				
				if(card.isSingleResultWorkFlow()) {
					
					Object wfResult = null;
					if(isNewWorkflowCard) {
						wfResult = WorkflowUtil.getWorkflowExpressionResult(workflowString, widgetStaticContext.getParamsJson(),true);
					}
					else {
						wfResult = WorkflowUtil.getWorkflowExpressionResult(workflowString, widgetStaticContext.getParamsJson(),criteria);
					}
					
					wfResult = CardUtil.getWorkflowResultForClient(wfResult, widgetStaticContext); // parsing data suitable for client
					result.put("result", wfResult);
					
					Object unit = CardUtil.getUnit(widgetStaticContext.getParamsJson());
					if(unit instanceof Unit) {
						result.put("unit", unit);
					}
					else {
						result.put("unitString", unit);
					}
				}
				else {
					Map<String, Object> expResult = WorkflowUtil.getExpressionResultMap(workflowString, widgetStaticContext.getParamsJson(),criteria);
					
					expResult = (Map<String, Object>) CardUtil.getWorkflowResultForClient(expResult, widgetStaticContext);
					
					result.put("result", expResult);
				}
				result.put("widget", widgetStaticContext);
				context.put(FacilioConstants.ContextNames.RESULT, result);
				return false;
			}
			
			else if(CardUtil.isExtraCard(widgetStaticContext.getStaticKey())) {
				
				result = new HashMap<>();
				
				if(widgetStaticContext.getStaticKey().equals("readingWithGraphCard")) {
					
					V2ReportAction reportAction = new V2ReportAction();
					if(widgetId != null) {
						reportAction.setCardWidgetId(widgetId);
					}
					else {
						reportAction.setCardParamJson(widgetStaticContext.getParamsJson());
					}
					
					if(startTime != null && endTime != null && startTime >0 && endTime > 0) {
						reportAction.setStartTime(startTime);
						reportAction.setEndTime(endTime);
					}
					
					reportAction.fetchReadingsFromCard();
					
					FacilioContext context1 = reportAction.getResultContext();
					
					result.put("result", context1);
					
					JSONObject params = widgetStaticContext.getParamsJson();
					
					 Map<Long, ReadingRuleAlarmMeta> alarmMeta = ReadingRuleAPI.fetchAlarmMeta((Long)params.get("parentId"), (Long)params.get("fieldId"));
					 
					List<AlarmContext> alarms = AlarmAPI.getAlarms(alarmMeta.keySet());
					
					result.put("alarmSeverity", AlarmAPI.getMaxSeverity(alarms));
					
					Object unit = CardUtil.getUnit(params);
					if(unit instanceof Unit) {
						result.put("unit", unit);
					}
					else {
						result.put("unitString", unit);
					}
					
					result.put("widget", widgetStaticContext);
					context.put(FacilioConstants.ContextNames.RESULT, result);
					return false;
					
				}
				else if (widgetStaticContext.getStaticKey().contains("alarmbarwidget") ) {

					paramsJson = widgetStaticContext.getParamsJson();
					context.put(FacilioConstants.ContextNames.RESULT, widgetStaticContext);
					return false;
				}
				else if(widgetStaticContext.getStaticKey().equals("resourceAlarmBar")) {
					
//					paramsJson = new JSONObject();
//					paramsJson.put("parentId", 4l);
//					paramsJson.put("dateOperator", 31);
					
					paramsJson = widgetStaticContext.getParamsJson();
					
					Long parentId = (Long) paramsJson.get("parentId");
					Long alarmId = (Long) paramsJson.get("alarmId");
					int dateOperator = Integer.parseInt(paramsJson.get("dateOperator").toString());
					String dateValue = (String) paramsJson.get("dateValue");
					Long ruleId = (Long) paramsJson.get("ruleId");
					Long parentAlarmId = (Long) paramsJson.get("parentAlarmId");
					Boolean rca = (Boolean) paramsJson.get("isRca");
					
					DateOperators operator = (DateOperators)Operator.getOperator(dateOperator);
					boolean fetchAlarmInfo = (boolean) context.getOrDefault(ContextNames.FETCH_ALARM_INFO, true);
					result = getResourceAlarmBar(parentId, ruleId, operator.getRange(dateValue), rca, alarmId, parentAlarmId, fetchAlarmInfo);
					context.put(FacilioConstants.ContextNames.RESULT, result);
					return false;
				}
				else if(widgetStaticContext.getStaticKey().contains("emrillFcu")) {
					
					JSONObject json = widgetStaticContext.getParamsJson();
					
					Long buildingId = (Long)json.get("buildingId");
					String levelString = (String) json.get("level");
					
					result = new HashMap<>();
					
					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
					
					List<FacilioField> newFieldList = new ArrayList<>(fields);
					
					newFieldList.add(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.RESOURCE)));
					
					GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
							.select(newFieldList)
							.table(modBean.getModule(FacilioConstants.ContextNames.RESOURCE).getTableName())
							.innerJoin("Assets").on("Assets.ID = Resources.ID")
							.innerJoin("BaseSpace").on("BaseSpace.ID = Resources.SPACE_ID")
							.andCustomWhere("BaseSpace.BUILDING_ID = ?",buildingId)
							.andCustomWhere("Resources.ORGID = ?",AccountUtil.getCurrentOrg().getId())
							.andCustomWhere("Assets.STRING_CF2 = ?", levelString);
					
					List<Map<String, Object>> props = selectBuilder.get();
					
					if(widgetStaticContext.getStaticKey().equals("emrillFcuList")) {
						DashboardUtil.getEmrillFCUListWidgetResult(props,result);
					}
					else {
						DashboardUtil.getEmrillFCUWidgetResult(result, props);
					}
					
					context.put(FacilioConstants.ContextNames.RESULT, result);
					return false;
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
								wfResult = WorkflowUtil.getWorkflowExpressionResult(widgetVsWorkflowContext.getWorkflowString(), paramMap,null);
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
	private Map<String,Object> getResourceAlarmBar(Long resourceId,Long ruleId, DateRange dateRange, Boolean isRCA, Long alarmId , Long parentAlarmId, boolean fetchAlarmInfo) throws Exception {
		
		Map<String,Object> result = new HashMap<>();
		List<Long> resourceIds = resourceId != null && resourceId != -1 ? Collections.singletonList(resourceId) : null;

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			List<AlarmOccurrenceContext> readingAlarmOccurrences = NewAlarmAPI.getReadingAlarmOccurrences(resourceIds, ruleId, -1, dateRange.getStartTime(), dateRange.getEndTime(), alarmId, parentAlarmId, isRCA);
			Map<Long, AlarmOccurrenceContext> alarmMap = new HashMap<>();
			JSONArray json = FetchReportAdditionalInfoCommand.splitAlarmOccurrence(readingAlarmOccurrences, dateRange, alarmMap);
			result.put("alarms", json);
			if (!fetchAlarmInfo && MapUtils.isNotEmpty(alarmMap)) {
				// Getting minimal info only
				Map<Long, JSONObject> alarmInfo = new HashMap<>();
				for(Map.Entry<Long, AlarmOccurrenceContext> entry: alarmMap.entrySet()) {
					AlarmOccurrenceContext occurrence = entry.getValue(); 
					JSONObject obj = new JSONObject();
					if (occurrence.getAlarm() != null) {
						JSONObject alarm = new JSONObject();
						alarm.put("subject", occurrence.getAlarm().getSubject());
						alarm.put("description", occurrence.getAlarm().getDescription());
						obj.put("alarm", alarm);
					}
					if (occurrence.getSeverity() != null) {
						JSONObject severity = new JSONObject();
						severity.put("id", occurrence.getSeverity().getId());
						obj.put("severity", severity);
					}
					if (occurrence.getPreviousSeverity() != null) {
						JSONObject severity = new JSONObject();
						severity.put("id", occurrence.getPreviousSeverity().getId());
						obj.put("previousSeverity", severity);
					}
					alarmInfo.put(entry.getKey(), obj);
				}
				result.put("alarmInfo", alarmInfo);
			}
			else {
				result.put("alarmInfo", alarmMap);
			}
		}

		else {
			if (!isRCA) {
				List<ReadingAlarmContext> allAlarms = AlarmAPI.getReadingAlarms(resourceIds, ruleId, -1, dateRange.getStartTime(), dateRange.getEndTime(), false);

				Map<Long, ReadingAlarmContext> alarmMap = new HashMap<>();
				JSONArray json = FetchReportAdditionalInfoCommand.splitAlarms(allAlarms, dateRange, alarmMap);
				result.put("alarms", json);
				result.put("alarmInfo", alarmMap);
			} else {
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(EventFieldFactory.getEventFields());

				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("subRuleId"), ruleId.toString(), NumberOperators.EQUALS));
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), dateRange.getStartTime() + "," + dateRange.getEndTime(), DateOperators.BETWEEN));

				List<EventContext> events = EventAPI.getEvent(criteria);

				if (events != null) {
					Map<Long, EventContext> eventIdMap = new HashMap<>();
					Map<Long, EventContext> eventCreationTimeMap = new HashMap<>();

					for (EventContext event : events) {
						eventIdMap.put(event.getId(), event);
						eventCreationTimeMap.put(event.getCreatedTime(), event);
					}
					result.put("events", FetchReportAdditionalInfoCommand.splitEvents(dateRange, eventCreationTimeMap, events));
					result.put("eventInfo", eventIdMap);
				}
			}
		}
		
		
		return result;
	}

}
