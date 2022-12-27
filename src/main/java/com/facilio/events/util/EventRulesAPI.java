package com.facilio.events.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class EventRulesAPI {
	
	public static final EventRuleContext getEventRule(long id) throws Exception {
		return getEventRule(id, true);
	}
	
	public static final EventRuleContext getEventRule(long id, boolean fetchChildren) throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
		List<FacilioField> fields = EventConstants.EventFieldFactory.getEventRulesFields();
		
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.orderBy("EXECUTION_ORDER")
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(id, module))
													;

		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		List<EventRuleContext> rules = getEventRulesFromProps(eventRulesProps, fetchChildren);
		if (rules != null && !rules.isEmpty()) {
			return rules.get(0);
		}
		return null;
	}
	
	public static final List<EventRuleContext> getActiveEventRules() throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
		List<FacilioField> fields = EventConstants.EventFieldFactory.getEventRulesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField active = fieldMap.get("active");
		
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.orderBy("EXECUTION_ORDER")
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(active, String.valueOf(true), BooleanOperators.IS))
													;

		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		return getEventRulesFromProps(eventRulesProps, true);
	}
	
	public static final List<EventRuleContext> getAllActiveEventRules() throws Exception {
		FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
		List<FacilioField> fields = EventConstants.EventFieldFactory.getEventRulesFields();
		FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.orderBy("EXECUTION_ORDER")
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													;

		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		return getEventRulesFromProps(eventRulesProps, true);
	}
	
	private static final List<EventRuleContext> getEventRulesFromProps(List<Map<String, Object>> eventRulesProps, boolean fetchChildren) throws Exception {
		if(eventRulesProps != null && !eventRulesProps.isEmpty()) {
			List<EventRuleContext> eventRules = new ArrayList<>();
			Set<Long> criteriaIds = new HashSet<>();
			Set<Long> workflowIds = new HashSet<>();
			for(Map<String, Object> eventRuleProp : eventRulesProps) {
				EventRuleContext rule = FieldUtil.getAsBeanFromMap(eventRuleProp, EventRuleContext.class);
				eventRules.add(rule);
				
				if (fetchChildren) {
					if (rule.getCriteriaId() != -1) {
						criteriaIds.add(rule.getCriteriaId());
					}
					if (rule.getWorkflowId() != -1) {
						workflowIds.add(rule.getWorkflowId());
					}
					if (rule.getTransformTemplateId() != -1) {
						rule.setTransformTemplate((JSONTemplate) TemplateAPI.getTemplate(rule.getTransformTemplateId()));
					}
				}
			}
			
			if (fetchChildren) {
				Map<Long, Criteria> criteriaMap = null;
				if (!criteriaIds.isEmpty()) {
					criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
				}
				
				Map<Long, WorkflowContext> workflowMap = null;
				if (!workflowIds.isEmpty()) {
					workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds, true);
				}
				
				for (EventRuleContext rule : eventRules) {
					if (rule.getCriteriaId() != -1) {
						rule.setCriteria(criteriaMap.get(rule.getCriteriaId()));
					}
					if (rule.getWorkflowId() != -1) {
						rule.setWorkflow(workflowMap.get(rule.getWorkflowId()));
					}
				}
			}
			
			return eventRules;
		}
		return null;
	}
	
	public static void updateChildIds (EventRuleContext rule) throws Exception {
		if (rule.getCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(rule.getCriteria(), AccountUtil.getCurrentOrg().getId());
			rule.setCriteriaId(criteriaId);
		}
		
		if (rule.getWorkflow() != null) {
			long workflowId = WorkflowUtil.addWorkflow(rule.getWorkflow());
			rule.setWorkflowId(workflowId);
		}
		
		if (rule.getTransformJson() != null) {
			JSONTemplate template = new JSONTemplate();
			template.setName(rule.getName());
			template.setContent(rule.getTransformJson().toJSONString());
			template.setWorkflow(TemplateAPI.getWorkflow(template));
			long templateId = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getId(), template);
			rule.setTransformTemplateId(templateId);
		}
	}
	
	//Old Code
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
	
	public static void deleteChildIds(EventRuleContext oldRule, EventRuleContext newRule) throws Exception {
		if((newRule == null || newRule.getCriteria() != null) && oldRule.getCriteriaId() != -1) {
			CriteriaAPI.deleteCriteria(oldRule.getCriteriaId());
		}
		
		if((newRule == null || newRule.getWorkflow() != null) && oldRule.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(oldRule.getWorkflowId());
		}
		
		if((newRule == null || newRule.getTransformJson() != null) && oldRule.getTransformTemplateId() != -1) {
			TemplateAPI.deleteTemplate(oldRule.getTransformTemplateId());
		}
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
			alarmTemplate.setWorkflow(TemplateAPI.getWorkflow(alarmTemplate));
			long alarmTemplateId = TemplateAPI.addJsonTemplate(orgId, alarmTemplate);
			eventRule.setTransformAlertTemplateId(alarmTemplateId);
		}
		
		if(eventRule.getThresholdCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(eventRule.getThresholdCriteria(), orgId);
			eventRule.setThresholdCriteriaId(criteriaId);
		}
		
		if(eventRule.getCoRelWorkflowXml() != null) {
			if(eventRule.getColRelActionEnum() == null) {
				throw new IllegalArgumentException("Co Relation action cannot be null when co relation expression exists");
			}
			
//			ExpressionContext expression = new ExpressionContext();
//			expression.setExpressionString(eventRule.getCoRelExpression());
//			long expressionId = ExpressionAPI.addExpression(expression);
//			eventRule.setCoRelExpressionId(expressionId);
			WorkflowContext workFlow = new WorkflowContext();
			workFlow.setWorkflowString(eventRule.getCoRelWorkflowXml());
			long workflowId = WorkflowUtil.addWorkflow(workFlow);
			eventRule.setCoRelWorkflowId(workflowId);
			
			if(eventRule.getCoRelTransformTemplate() != null) {
				JSONTemplate coRelTemplate = new JSONTemplate();
				coRelTemplate.setName(eventRule.getName());
				coRelTemplate.setContent(eventRule.getCoRelTransformTemplate().toJSONString());
				coRelTemplate.setWorkflow(TemplateAPI.getWorkflow(coRelTemplate));
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

	public static EventRule getCompleteEventRule(long id) throws Exception {
		// TODO Auto-generated method stub
		long orgId = AccountUtil.getCurrentOrg().getId();
		EventRule eventRule = getEventRule(orgId, id);
		if (eventRule != null) {
			if (eventRule.getBaseCriteriaId() != -1) {
				eventRule.setBaseCriteria(CriteriaAPI.getCriteria(orgId, eventRule.getBaseCriteriaId()));
			}
			
			if (eventRule.getTransformCriteriaId() != -1) {
				eventRule.setTransformCriteria(CriteriaAPI.getCriteria(orgId, eventRule.getTransformCriteriaId()));
				
				if (eventRule.getTransformAlertTemplateId() != -1) {
					JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(eventRule.getTransformAlertTemplateId());
					JSONParser parser = new JSONParser();
					eventRule.setTransformTemplate((JSONObject) parser.parse(template.getContent()));
				}
			}
			
			if (eventRule.getThresholdCriteriaId() != -1) {
				eventRule.setThresholdCriteria(CriteriaAPI.getCriteria(orgId, eventRule.getThresholdCriteriaId()));
			}
			
			if (eventRule.getCoRelWorkflowId() != -1) {
				WorkflowContext workFlow = WorkflowUtil.getWorkflowContext(eventRule.getCoRelWorkflowId());
				eventRule.setCoRelWorkflowXml(workFlow.getWorkflowString());
				
				if(eventRule.getCoRelTransformTemplateId() != -1) {
					JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(eventRule.getCoRelTransformTemplateId());
					JSONParser parser = new JSONParser();
					eventRule.setCoRelTransformTemplate((JSONObject) parser.parse(template.getContent()));
				}
			}
			
			return eventRule;
		}
		return null;
	}
}
