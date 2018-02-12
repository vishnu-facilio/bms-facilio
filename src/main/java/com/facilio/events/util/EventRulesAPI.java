package com.facilio.events.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ExpressionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ExpressionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.sql.GenericSelectRecordBuilder;

public class EventRulesAPI {
	public static final EventRule getEventRule(long orgId, long eventRuleId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
												.select(EventConstants.EventFieldFactory.getEventRuleFields())
												.table("Event_Rule")
												.andCustomWhere("ORGID = ? AND EVENT_RULE_ID = ?", orgId, eventRuleId);
		
		List<Map<String, Object>> eventRules = rulebuilder.get();
		if(eventRules != null && !eventRules.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(eventRules.get(0), EventRule.class);
		}
		return null;
	}
	
	public static final List<EventRule> getEventRules(long orgId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventRuleFields())
														.table(EventConstants.EventModuleFactory.getEventRuleModule().getTableName())
														.orderBy("RULE_ORDER")
														.andCustomWhere("ORGID = ?", orgId);

		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		if(eventRulesProps != null && !eventRulesProps.isEmpty()) {
			List<EventRule> eventRules = new ArrayList<>();
			for(Map<String, Object> eventRuleProp : eventRulesProps) {
				eventRules.add(FieldUtil.getAsBeanFromMap(eventRuleProp, EventRule.class));
			}
			return eventRules;
 		}
		return null;
	}
	
	public static final List<EventToAlarmFieldMapping> getEventToAlarmFieldMappings(long orgId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventToAlarmFieldMappingFields())
														.table(EventConstants.EventModuleFactory.getEventToAlarmFieldMappingModule().getTableName())
														.orderBy("MAPPING_ORDER")
														.andCustomWhere("ORGID = ?", orgId);
		
		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		if(eventRulesProps != null && !eventRulesProps.isEmpty()) {
			List<EventToAlarmFieldMapping> eventTransformMappings = new ArrayList<>();
			for(Map<String, Object> eventRuleProp : eventRulesProps) {
					eventTransformMappings.add(FieldUtil.getAsBeanFromMap(eventRuleProp, EventToAlarmFieldMapping.class));
			}
			return eventTransformMappings;
		}
		return null;
	}
	
	public static final void updateEventRuleChildIds(EventRule eventRule, long orgId) throws Exception {
		if(eventRule.getBaseCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(eventRule.getBaseCriteria(),orgId);
			eventRule.setBaseCriteriaId(criteriaId);
		}
		
		if(eventRule.getTransformCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(eventRule.getTransformCriteria(),orgId);
			eventRule.setTransformCriteriaId(criteriaId);
		}
		
		if(eventRule.getTransformTemplate() != null) {
			JSONTemplate alarmTemplate = new JSONTemplate();
			alarmTemplate.setName(eventRule.getName());
			alarmTemplate.setContent(eventRule.getTransformTemplate().toJSONString());
			long alarmTemplateId = TemplateAPI.addJsonTemplate(orgId, alarmTemplate);
			eventRule.setTransformAlertTemplateId(alarmTemplateId);
		}
		
		if(eventRule.getThresholdCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(eventRule.getThresholdCriteria(), orgId);
			eventRule.setThresholdCriteriaId(criteriaId);
		}
		
		if(eventRule.getCoRelExpression() != null) {
			if(eventRule.getColRelActionEnum() == null) {
				throw new IllegalArgumentException("Co Relation action cannot be null when co relation expression exists");
			}
			
			ExpressionContext expression = new ExpressionContext();
			expression.setExpressionString(eventRule.getCoRelExpression());
			long expressionId = ExpressionAPI.addExpression(expression);
			eventRule.setCoRelExpressionId(expressionId);
			
			if(eventRule.getCoRelTransformTemplate() != null) {
				JSONTemplate coRelTemplate = new JSONTemplate();
				coRelTemplate.setName(eventRule.getName());
				coRelTemplate.setContent(eventRule.getCoRelTransformTemplate().toJSONString());
				long coRelTemplateId = TemplateAPI.addJsonTemplate(orgId, coRelTemplate);
				eventRule.setCoRelTransformTemplateId(coRelTemplateId);
			}
		}
	}
	
	public static final EventProperty getEventProperty(long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(EventConstants.EventModuleFactory.getEventPropertyModule().getTableName())
														.select(EventConstants.EventFieldFactory.getEventPropertyFields())
														.andCustomWhere("ORGID = ?", orgId);
		
		return FieldUtil.getAsBeanFromMap(selectBuilder.get().get(0), EventProperty.class);
	}
}
