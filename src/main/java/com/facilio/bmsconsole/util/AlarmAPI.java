package com.facilio.bmsconsole.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;

public class AlarmAPI {
	
	public static void loadExtendedAlarms(List<AlarmContext> alarms) throws Exception {
		if (alarms != null && !alarms.isEmpty()) {
			Map<SourceType, List<Long>> typeWiseIds = new HashMap<>();
			for (AlarmContext alarm : alarms) {
				List<Long> ids = typeWiseIds.get(alarm.getSourceTypeEnum());
				if (ids == null) {
					ids = new ArrayList<>();
					typeWiseIds.put(alarm.getSourceTypeEnum(), ids);
				}
				ids.add(alarm.getId());
			}
			Map<SourceType, Map<Long, ? extends AlarmContext>> typeWiseAlarms = getTypeWiseAlarms(typeWiseIds);
			
			for (int i = 0; i < alarms.size(); i++) {
				AlarmContext alarm = alarms.get(i);
				SourceType type = alarm.getSourceTypeEnum();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM:
							alarms.set(i, typeWiseAlarms.get(alarm.getSourceTypeEnum()).get(alarm.getId()));
							break;
						default:
							break;
					}
				}
			}
		}
	}
	
	private static Map<SourceType, Map<Long, ? extends AlarmContext>> getTypeWiseAlarms (Map<SourceType, List<Long>> typeWiseIds) throws Exception {
		Map<SourceType, Map<Long,? extends AlarmContext>> typewiseAlarms = new HashMap<>();
		
		if (typeWiseIds != null && !typeWiseIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map.Entry<SourceType, List<Long>> entry : typeWiseIds.entrySet()) {
				SourceType type = entry.getKey();
				if (type != null) {
					switch (type) {
						case THRESHOLD_ALARM: {
							typewiseAlarms.put(entry.getKey(), fetchExtendedAlarms(modBean.getModule(FacilioConstants.ContextNames.READING_ALARM), modBean.getAllFields(FacilioConstants.ContextNames.READING_ALARM), entry.getValue(), ReadingAlarmContext.class));
						}break;
						default:
							break;
					}
				}
			}
		}
		
		return typewiseAlarms;
	}
	
	private static <E extends AlarmContext> Map<Long, E> fetchExtendedAlarms(FacilioModule module, List<FacilioField> fields, List<Long> ids, Class<E> alarmClass) throws Exception {
		SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>()
																		.select(fields)
																		.module(module)
																		.beanClass(alarmClass)
																		.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return selectBuilder.getAsMap();
	}
	
	public static void updateAlarmDetailsInTicket(AlarmContext sourceAlarm, AlarmContext destinationAlarm) throws Exception {
		
		TicketCategoryContext category =  null;
		if (sourceAlarm.getAlarmTypeEnum() != null) {
			switch (sourceAlarm.getAlarmTypeEnum()) {
				case FIRE:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Fire Safety");
					break;
				case HVAC:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "HVAC");
					break;
				case ENERGY:
					category = TicketAPI.getCategory(AccountUtil.getCurrentOrg().getOrgId(), "Energy");
					break;
				default:
					break;
			}
			if(category != null) {
				destinationAlarm.setCategory(category);
			}
		}
		
		if(sourceAlarm != destinationAlarm && destinationAlarm.getSeverity() != null) {
			destinationAlarm.setPreviousSeverity(sourceAlarm.getSeverity());
		}
		
		ResourceContext resource = sourceAlarm.getResource();
		if(resource != null && resource.getId() != -1) {
			resource = ResourceAPI.getResource(resource.getId());
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					if(sourceAlarm.getNode() != null) {
						String description;
						if(sourceAlarm.isAcknowledged()) {
							description = MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode(), resource.getName());
						}
						else {
							description = MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode(), resource.getName());
						}
						destinationAlarm.setDescription(description);
					}
					break;
				case ASSET:
					String description;
					BaseSpaceContext space = SpaceAPI.getBaseSpace(resource.getSpaceId());
					
					if(sourceAlarm.isAcknowledged()) {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					else {
						if(space != null) {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}\nLocation - {2}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName(), space.getName());
						}
						else {
							description = MessageFormat.format("A{0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal() != null? " "+sourceAlarm.getAlarmTypeVal():"n",resource.getName());
						}
					}
					destinationAlarm.setDescription(description);
					break;
			}
		}
		else {
			if(sourceAlarm.isAcknowledged()) {
				destinationAlarm.setDescription(MessageFormat.format("A {0} alarm raised from {1} has been acknowledged.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode()));
			}
			else {
				destinationAlarm.setDescription(MessageFormat.format("A {0} alarm raised from {1} is waiting for acknowledgement.\n\nAlarm details : \nAlarm Type - {0}\nSensor - {1}",sourceAlarm.getAlarmTypeVal(),sourceAlarm.getNode()));
			}
		}
	}
	
	public static String sendAlarmSMS(AlarmContext alarm, String to, String message) throws Exception {
		ResourceContext resource = alarm.getResource();
		if(resource != null) {
			resource = ResourceAPI.getResource(resource.getId());
			String spaceName = null;
			switch (resource.getResourceTypeEnum()) {
				case SPACE:
					spaceName = resource.getName();
					break;
				case ASSET:
					spaceName = SpaceAPI.getBaseSpace(resource.getSpaceId()).getName();
					break;
			}
			String sms = null;
			if(message != null && !message.isEmpty()) {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}, {3}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName, message);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}, {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), message);
				}
			}
			else {
				if(spaceName != null) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} @ {2}", alarm.getAlarmTypeVal(), alarm.getSubject(), spaceName);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1}", alarm.getAlarmTypeVal(), alarm.getSubject());
				}
			}
			JSONObject json = new JSONObject();
			json.put("to", to);
			json.put("message", sms);
			return SMSUtil.sendSMS(json);
		}
		return null;
	}
	
	public static long addAlarmEntity() throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentAccount().getOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table("Alarm_Entity")
				.fields(FieldFactory.getAlarmEntityFields())
				.addRecord(prop);
		insertBuilder.save();
		return (long) prop.get("id");
	}
	
	public static AlarmSeverityContext getAlarmSeverity(String severity) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("SEVERITY = ?", severity);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static AlarmSeverityContext getAlarmSeverity(long severityId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class)
																		.andCustomWhere("ID = ?", severityId);
		
		List<AlarmSeverityContext> severities = selectBuilder.get();
		if(severities != null && !severities.isEmpty()) {
			return severities.get(0);
		}
		return null;
	}
	
	public static List<AlarmSeverityContext> getAlarmSeverityList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<AlarmSeverityContext> selectBuilder = new SelectRecordsBuilder<AlarmSeverityContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM_SEVERITY))
																		.moduleName(FacilioConstants.ContextNames.ALARM_SEVERITY)
																		.beanClass(AlarmSeverityContext.class);
		
		return selectBuilder.get();
	}
	
	public static AlarmType getAlarmTypeFromResource(long resourceId) throws Exception {
		if (resourceId != -1) {
			ResourceContext resource = ResourceAPI.getExtendedResource(resourceId);
			if (resource != null && resource.getResourceTypeEnum() == ResourceType.ASSET) {
				AssetContext asset = (AssetContext) resource;
				if (asset.getCategory() != null && asset.getCategory().getId() != -1) {
					return getAlarmTypeFromAssetCategory(asset.getCategory().getId());
				}
			}
		}
		return null;
	}
	
	public static AlarmType getAlarmTypeFromAssetCategory(long categoryId) throws Exception {
		AssetCategoryContext category = AssetsAPI.getCategoryForAsset(categoryId);
		
		switch (category.getTypeEnum()) {
			case ENERGY:
				return AlarmType.ENERGY;
			case FIRE:
				return AlarmType.FIRE;
			case HVAC:
				return AlarmType.HVAC;
			case MISC:
				return AlarmType.MAINTENANCE;
			default:
				return null;
		}
	}
	
	public static void addReadingAlarmProps(JSONObject obj, ReadingRuleContext rule, ReadingContext reading) throws Exception {
		obj.put("readingFieldId", rule.getReadingFieldId());
		
		if (rule.getBaselineId() != -1) {
			obj.put("baselineId", rule.getBaselineId());
		}
		obj.put("sourceType", SourceType.THRESHOLD_ALARM.getIntVal());
		DateRange range = getRange(rule, reading);
		obj.put("startTime", range.getStartTime());
		if (range.getEndTime() != -1) {
			obj.put("endTime", range.getEndTime());
		}
		
		obj.put("readingMessage", getMessage(rule, range, reading));
		obj.put("resourceId", reading.getParentId());
		
		String resourceName = ((ResourceContext)reading.getParent()).getName();
		obj.put("source", resourceName);
		obj.put("node", resourceName);
		obj.put("timestamp", reading.getActualTtime());
	}
	
	private static DateRange getRange(ReadingRuleContext rule, ReadingContext reading) {
		DateRange range = null;
		switch (rule.getThresholdTypeEnum()) {
			case SIMPLE:
				if (rule.getCriteria() != null) {
					range = new DateRange();
					range.setStartTime(reading.getTtime());
				}
				else {
					WorkflowContext workflow = rule.getWorkflow();
					ExpressionContext expression = workflow.getExpressions().get(0);
					if (expression.getLimit() != null) {
						range = new DateRange();
						range.setStartTime(reading.getTtime());
					}
					else {
						Condition condition = expression.getCriteria().getConditions().get(2);
						range = ((DateOperators) condition.getOperator()).getRange(condition.getValue());
					}
				}
				break;
			case AGGREGATION:
			case BASE_LINE:
				range = DateOperators.LAST_N_HOURS.getRange(String.valueOf(rule.getDateRange()));
				break;
			case FLAPPING:
				range = new DateRange();
				range.setEndTime(reading.getTtime());
				range.setStartTime(range.getEndTime() - rule.getFlapInterval());
				break;
			case ADVANCED:
			case FUNCTION:
				range = new DateRange();
				range.setStartTime(reading.getTtime());
				break;
		}
		return range;
	}
	
	private static String getMessage(ReadingRuleContext rule, DateRange range, ReadingContext reading) throws Exception {
		StringBuilder msgBuilder = new StringBuilder();
		if (rule.getAggregation() != null) {
			if(rule.getDateRange() == 1) {
				msgBuilder.append("Hourly ")
							.append(rule.getAggregation());
			}
			else {
				msgBuilder.append(WordUtils.capitalize(rule.getAggregation()));
			}
			msgBuilder.append(" of ");
		}
		msgBuilder.append("'")
					.append(rule.getReadingField().getDisplayName())
					.append("' ");
		
		NumberOperators operator = (NumberOperators) Operator.OPERATOR_MAP.get(rule.getOperatorId());
		switch (rule.getThresholdTypeEnum()) {
			case SIMPLE:
				appendSimpleMsg(msgBuilder, operator, rule, reading);
				appendOccurences(msgBuilder, rule);
				break;
			case AGGREGATION:
				appendSimpleMsg(msgBuilder, operator, rule, reading);
				break;
			case BASE_LINE:
				appendBaseLineMsg(msgBuilder, operator, rule);
				break;
			case FLAPPING:
				appendFlappingMsg(msgBuilder, rule);
				break;
			case ADVANCED:
				appendAdvancedMsg(msgBuilder, rule, reading);
				break;
			case FUNCTION:
				appendFunctionMsg(msgBuilder, rule, reading);
				break;
		}
		
		if (range.getEndTime() != -1) {
			msgBuilder.append(" between ")
					.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(FacilioConstants.READABLE_DATE_FORMAT))
					.append(" and ")
					.append(DateTimeUtil.getZonedDateTime(range.getEndTime()).format(FacilioConstants.READABLE_DATE_FORMAT));
		}
		else {
			msgBuilder.append(" at ")
						.append(DateTimeUtil.getZonedDateTime(range.getStartTime()).format(FacilioConstants.READABLE_DATE_FORMAT));
		}
		
		return msgBuilder.toString();
	}
	
	private static void appendOccurences (StringBuilder msgBuilder, ReadingRuleContext rule) {
		WorkflowContext workflow = rule.getWorkflow();
		if (workflow != null) {
			ExpressionContext expression = workflow.getExpressions().get(0);
			if (expression.getAggregateCondition() != null && !expression.getAggregateCondition().isEmpty()) {
				msgBuilder.append(" ")
							.append(getInWords(Integer.parseInt(rule.getPercentage())));
				if (expression.getLimit() != null) {
					msgBuilder.append(" consecutively");
				}
			}
		}
	}
	
	private static void appendSimpleMsg(StringBuilder msgBuilder, NumberOperators operator, ReadingRuleContext rule, ReadingContext reading) {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was ");
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't ");
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went below ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("exceeded ");
				break;
		}
		
		String value = null;
		if (rule.getWorkflow() != null) {
			ExpressionContext expr = rule.getWorkflow().getExpressions().get(0);
			if (expr.getAggregateCondition() != null && !expr.getAggregateCondition().isEmpty()) {
				Condition aggrCondition = expr.getAggregateCondition().get(0);
				value = aggrCondition.getValue();
			}
		}
		if (value == null) {
			value = rule.getPercentage();
		}
		
		if ("${previousValue}".equals(value)) {
			msgBuilder.append("previous value (")
						.append(reading.getReading(rule.getReadingField().getName()))
						.append(")");
		}
		else {
			msgBuilder.append(value);
		}
		appendUnit(msgBuilder, rule);
	}
	
	private static void appendBaseLineMsg (StringBuilder msgBuilder, NumberOperators operator, ReadingRuleContext rule) throws Exception {
		switch (operator) {
			case EQUALS:
				msgBuilder.append("was along ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				break;
			case NOT_EQUALS:
				msgBuilder.append("wasn't along ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				break;
			case LESS_THAN:
			case LESS_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				msgBuilder.append("lower than ");
				break;
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
				msgBuilder.append("went ");
				updatePercentage(rule.getPercentage(), msgBuilder);
				msgBuilder.append("higher than ");
				break;
		}
		
		BaseLineContext bl = BaseLineAPI.getBaseLine(rule.getBaselineId());
		msgBuilder.append("the ");
		msgBuilder.append("base line ")
					.append("'")
					.append(bl.getName())
					.append("'");
	}
	
	private static String getInWords (int val) {
		switch (val) {
			case 1:
				return "once";
			case 2:
				return "twice";
			case 3:
				return "thrice";
			default:
				return val+" times";
		}
	}
	
	private static void appendFlappingMsg (StringBuilder msgBuilder, ReadingRuleContext rule) {
		msgBuilder.append("flapped ")
					.append(getInWords(rule.getFlapFrequency()));
		
		switch (rule.getReadingField().getDataTypeEnum()) {
			case NUMBER:
			case DECIMAL:
				msgBuilder.append(" below ")
							.append(rule.getMinFlapValue());
				appendUnit(msgBuilder, rule);
				msgBuilder.append(" and beyond ")
							.append(rule.getMaxFlapValue());
				appendUnit(msgBuilder, rule);
				break;
			default:
				break;
		}
	}
	
	private static void appendAdvancedMsg (StringBuilder msgBuilder, ReadingRuleContext rule, ReadingContext reading) {
		msgBuilder.append("recorded ")
					.append(reading.getReading(rule.getReadingField().getName()));
		appendUnit(msgBuilder, rule);
		
		msgBuilder.append(" when the complex condition set in '")
					.append(rule.getName())
					.append("'")
					.append(" rule evaluated to true");
	}
	
	private static void appendFunctionMsg (StringBuilder msgBuilder, ReadingRuleContext rule, ReadingContext reading) {
		msgBuilder.append("recorded ")
					.append(reading.getReading(rule.getReadingField().getName()));
		appendUnit(msgBuilder, rule);
		
		String functionName = null;
		if (rule.getWorkflow() != null) {
			ExpressionContext expr = rule.getWorkflow().getExpressions().get(1);
			functionName = expr.getDefaultFunctionContext().getFunctionName();
		}
		
		msgBuilder.append(" when the function (")
					.append(functionName)
					.append(") set in '")
					.append(rule.getName())
					.append("'")
					.append(" rule evaluated to true");
	}
	
	private static void appendUnit(StringBuilder msgBuilder, ReadingRuleContext rule) {
		if (rule.getReadingField() instanceof NumberField && ((NumberField)rule.getReadingField()).getUnit() != null) {
			msgBuilder.append(((NumberField)rule.getReadingField()).getUnit());
		}
	}
	
	private static void updatePercentage(String percentage, StringBuilder msgBuilder) {
		if (percentage != null && !percentage.equals("0")) {
			msgBuilder.append(percentage)
						.append("% ");
		}
	}
}
