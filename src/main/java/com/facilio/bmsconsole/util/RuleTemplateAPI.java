package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleTemplateAPI {
	
	public static Map<String, Object> addRuleTemplateRel(long templateId, long ruleId) throws SQLException, RuntimeException {
			Map<String, Object> ruleTemplateProps = new HashMap<String, Object>();
			ruleTemplateProps.put("ruleId", ruleId);
			ruleTemplateProps.put("defaultTemplateId", templateId);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table("Rule_Templates_Rel")
														.fields(FieldFactory.getRuleTemplateRelFields())
														.addRecord(ruleTemplateProps);
			insertBuilder.save();
			return ruleTemplateProps;
	}
	
	public static ReadingRuleContext convertByRuleType (JSONObject rulesObject) throws Exception {
		ReadingRuleContext rule = new ReadingRuleContext();
		ObjectMapper mapper = FieldUtil.getMapper(WorkflowContext.class);
		JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray((JSONObject) rulesObject.get("workflow"));
		long thresholdType = (long) rulesObject.get("thresholdType");
		List<WorkflowContext> list = mapper.readValue(JSONArray.toJSONString(fieldJsons), mapper.getTypeFactory().constructCollectionType(List.class, WorkflowContext.class));
		rule.setWorkflow(list.get(0));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		System.out.println("Module Name----> " + rulesObject.get("moduleName"));
		System.out.println("threshold_metric Name----> " + rulesObject.get("threshold_metric"));
		if (rulesObject.get("moduleName") != null && rulesObject.get("threshold_metric") != null) {
			FacilioModule preModule = modBean.getModule((String) rulesObject.get("moduleName"));
			FacilioField preRequesitefield = modBean.getField((String) rulesObject.get("threshold_metric"), preModule.getName());
			rule.setReadingFieldId(preRequesitefield.getId());
		}
		rule.setThresholdType((int) thresholdType);
		switch (ThresholdType.valueOf(rule.getThresholdType())) {
			case SIMPLE :
				long operator = (long) rulesObject.get("operatorId");
				rule.setOperatorId((int) operator);
				rule.setPercentage((String) rulesObject.get("percentage"));
			break;
			case AGGREGATION:
			break;
			case BASE_LINE:
			break;
			case FLAPPING:
			break;
			case ADVANCED:
			break;
			case FUNCTION:
			break;
		}
		return rule;
	}
	@SuppressWarnings("unchecked")
	public static JSONObject setPlaceHolder (JSONObject jsonTemplate) throws Exception {
		
		
		JSONObject rules = (JSONObject) jsonTemplate.get("fdd_rule");
		JSONObject placeholders = (JSONObject) jsonTemplate.get("placeHolder");
		if (placeholders != null) {
			Map<String, Object> placeHolderMap = new HashMap<>();
			if (placeholders != null) {
				placeholders.keySet().forEach(keyStr -> {
					Map <String , Object> keyvalue =  (Map<String, Object>) placeholders.get(keyStr);
					placeHolderMap.put((String) keyvalue.get("uniqueId"), keyvalue.get("default_value"));
				});
			}
			JSONParser parser = new JSONParser();
			String resolvedString =  StringSubstitutor.replace(rules, placeHolderMap);
			JSONObject obj = (JSONObject) parser.parse(resolvedString);
			return obj;
		}
		else {
			return rules;
		}
	}
	@SuppressWarnings("unchecked")
	public static AlarmRuleContext convertTemplateToRule (JSONObject ruleObj) throws Exception {
			
			AssetCategoryContext assetCategory = AssetsAPI.getCategory((String) ruleObj.get("category"));
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule((String) ruleObj.get("moduleName"));
			
			AlarmRuleContext alarmRule = new AlarmRuleContext();
			if (ruleObj.get("preRequsite") != null) {
				alarmRule.setPreRequsite(convertByRuleType((JSONObject) ruleObj.get("preRequsite")));
			}
			else {
				
				alarmRule.setPreRequsite(new ReadingRuleContext());
			}
			alarmRule.setAlarmTriggerRule(convertByRuleType((JSONObject) ruleObj.get("alarmCondition")));
			if ((JSONObject) ruleObj.get("alarmClear") != null) {
				alarmRule.setAlarmClearRule(convertByRuleType((JSONObject) ruleObj.get("preRequsite")));
			} else {
				alarmRule.setIsAutoClear(true);
			}
			alarmRule.getAlarmTriggerRule().setName("Trigger Name");
			alarmRule.getPreRequsite().setEvent(new WorkflowEventContext());
			alarmRule.getPreRequsite().getEvent().setModuleId(module.getModuleId());
			alarmRule.getPreRequsite().getEvent().setActivityType(EventType.CREATE.getValue());
			
			alarmRule.getPreRequsite().setName((String) ruleObj.get("name"));
			alarmRule.getPreRequsite().setDescription((String) ruleObj.get("description"));
			alarmRule.getPreRequsite().setAssetCategoryId(assetCategory.getId());
			
			ActionContext action = new ActionContext();
			action.setActionType(ActionType.ADD_ALARM);
			
			JSONObject possible = new JSONObject();
			JSONArray fieldMatcher = new JSONArray();
			JSONObject content = new JSONObject();
			content.put("field", "problem");
			content.put("value", ruleObj.get("problem"));
			fieldMatcher.add(content);
			content = new JSONObject();
			content.put("field", "message");
			content.put("value", ruleObj.get("name"));
			fieldMatcher.add(content);
			content = new JSONObject();
			content.put("field", "possibleCauses");
			content.put("value", ruleObj.get("possible_causes"));
			fieldMatcher.add(content);
			content = new JSONObject();
			content.put("field", "severity");
			content.put("value", ruleObj.get("severity"));
			fieldMatcher.add(content);
			content = new JSONObject();
			content.put("field", "recommendation");
			content.put("value", ruleObj.get("possible_solution"));
			fieldMatcher.add(content);
			content = new JSONObject();
			possible.put("fieldMatcher", fieldMatcher);
			action.setTemplateJson(possible);
			alarmRule.getAlarmTriggerRule().setActions(Collections.singletonList(action));
			return alarmRule;
	}
}