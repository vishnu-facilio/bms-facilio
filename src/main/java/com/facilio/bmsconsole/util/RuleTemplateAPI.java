package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
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
	
	public static List<Map<String, Object>> getAppliedTemplates () throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table("Rule_Templates_Rel")
														.select(FieldFactory.getRuleTemplateRelFields());
		
			
			List<Map<String, Object>> props = selectBuilder.get();
			HashMap<Long, Long> rels = new HashMap<Long, Long>();
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop: props) {
					Long defaultTemplateId = (Long) prop.get("defaultTemplateId");
					Long ruleId = (Long) prop.get("ruleId");
					rels.put(defaultTemplateId, ruleId);

				}
			}
		 return selectBuilder.get();
	}
	
	public static ReadingRuleContext convertByRuleType (JSONObject rulesObject) throws Exception {
		ReadingRuleContext rule = new ReadingRuleContext();
		ObjectMapper mapper = FieldUtil.getMapper(WorkflowContext.class);
		JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray(rulesObject.get("workflow"));
		long thresholdType = (long) rulesObject.get("thresholdType");
		List<WorkflowContext> list = mapper.readValue(JSONArray.toJSONString(fieldJsons), mapper.getTypeFactory().constructCollectionType(List.class, WorkflowContext.class));
		rule.setWorkflow(list.get(0));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
				if (rulesObject.get("occurences") != null) {
					rule.setOccurences((int) rulesObject.get("occurences"));
				}
				if (rulesObject.get("interval") != null) {
					rule.setInterval((long) rulesObject.get("interval"));
				}
				if (rulesObject.get("consecutive") != null) {
					rule.setConsecutive((Boolean) rulesObject.get("interval"));
				}
			break;
			case AGGREGATION:
			break;
			case BASE_LINE:
			break;
			case FLAPPING:
			break;
			case ADVANCED:
				if (rulesObject.get("occurences") != null) {
					rule.setOccurences(Integer.valueOf((String) rulesObject.get("occurences")));
				}
				if (rulesObject.get("for") != null) {
					rule.setOverPeriod(Long.valueOf((String) rulesObject.get("for")));
				}
				if (rulesObject.get("consecutive") != null) {
					rule.setConsecutive(Boolean.parseBoolean((String) rulesObject.get("consecutive")));
				}
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
		Map<String, Object> placeHolderMap = new HashMap<>();
		if (placeholders != null) {
			if (placeholders != null) {
				placeholders.keySet().forEach(keyStr -> {
					Map <String , Object> keyvalue =  (Map<String, Object>) placeholders.get(keyStr);
					placeHolderMap.put( (String) keyvalue.get("uniqueId"), keyvalue.get("default_value"));
				});
			}
		}
		else {
			return rules;
		}		 
		String resolvedString = rules.toString();
		for(String key : placeHolderMap.keySet()) {
			String val = null;
			if (placeHolderMap.get(key) != null) {
				val = placeHolderMap.get(key).toString();
			}
			else {
				val = "";
			}
			
			String var = "${"+key+"}";
			String varRegex = "\\$\\{"+key+"\\}";
			if(resolvedString.contains(var)) {
				resolvedString = resolvedString.replaceAll(varRegex, val);
			}
		}
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(resolvedString);
		return obj;
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
		AlarmSeverityContext severity = AlarmAPI.getAlarmSeverity((String) ruleObj.get("severity"));
		ReadingRuleContext preRequsite = alarmRule.getPreRequsite();
		preRequsite.setAlarmSeverityId(severity.getId());
		JSONObject selectionResource = (JSONObject) ruleObj.get("ASSET_SELECTION_CRITERIA");
		if (!selectionResource.get("includeResource").toString().contains("${includeResource"))  {
			System.out.println(selectionResource.get("includeResource"));
			JSONParser parser = new JSONParser();
			Object obj  = parser.parse((String) selectionResource.get("includeResource"));
			JSONArray resourceList = (JSONArray) obj;
			preRequsite.setIncludedResources(resourceList);
		}
		if (!selectionResource.get("excludeResource").toString().contains("${excludeResource}"))  {
			preRequsite.setExcludedResources((List<Long>) selectionResource.get("excludeResource"));
		}
		alarmRule.setAlarmTriggerRule(convertByRuleType((JSONObject) ruleObj.get("alarmCondition")));
		FacilioModule readingModule = modBean.getModule((String) ruleObj.get("moduleName"));
		FacilioField readingfield = modBean.getField((String) ruleObj.get("threshold_metric"), readingModule.getName());
		alarmRule.getAlarmTriggerRule().setReadingFieldId(readingfield.getId());
		if ((JSONObject) ruleObj.get("alarmClear") != null) {
			alarmRule.setAlarmClearRule(convertByRuleType((JSONObject) ruleObj.get("preRequsite")));
		} else {
			alarmRule.setIsAutoClear(true);
		}
		alarmRule.getAlarmTriggerRule().setName("Trigger Name");
		preRequsite.setModuleId(module.getModuleId());
		preRequsite.setActivityType(EventType.CREATE.getValue());

		preRequsite.setName((String) ruleObj.get("name"));
		preRequsite.setDescription((String) ruleObj.get("description"));
		preRequsite.setAssetCategoryId(assetCategory.getId());

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