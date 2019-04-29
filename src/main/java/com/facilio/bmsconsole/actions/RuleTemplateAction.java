package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Chain;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleTemplateAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String result;
	public String getRes() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	@SuppressWarnings("unchecked")
	public String createRulefromTemplates () {
		JSONObject templateJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.RULE, (int) id).getJson();
		JSONObject rules = (JSONObject) templateJson.get("fdd_rule");
		JSONObject placeholders = (JSONObject) rules.get("placeHolder");
		try {
			Map<String, Object> placeHolderMap = new HashMap<>();
			placeholders.keySet().forEach(keyStr -> {
				final JSONObject keyvalue = (JSONObject) placeholders.get(keyStr);
				placeHolderMap.put((String) keyvalue.get("uniqueId"), keyvalue.get("default_value"));
			});
			JSONParser parser = new JSONParser();
			String resolvedString =  StringSubstitutor.replace(rules, placeHolderMap);
			JSONObject obj = (JSONObject) parser.parse(resolvedString);
			JSONObject rulesObj = (JSONObject) obj.get("rules");
			
			Set<String> ruleskeys = rulesObj.keySet();
			AssetCategoryContext assetCategory = AssetsAPI.getCategory((String) obj.get("category"));
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule((String) obj.get("moduleName"));
			
			AlarmRuleContext alarmRule = new AlarmRuleContext();
			alarmRule.setPreRequsite(new ReadingRuleContext());
			alarmRule.setAlarmTriggerRule(new ReadingRuleContext());
			alarmRule.getPreRequsite().setName((String) obj.get("name"));
			alarmRule.getPreRequsite().setDescription((String) obj.get("description"));
			alarmRule.getPreRequsite().setAssetCategoryId(assetCategory.getId());
			
			FacilioField field = modBean.getField((String) obj.get("threshold_metric"), module.getName());
			
			alarmRule.getAlarmTriggerRule().setReadingFieldId(field.getFieldId());
			alarmRule.getPreRequsite().setEvent(new WorkflowEventContext());
			alarmRule.getPreRequsite().getEvent().setModuleId(module.getModuleId());
			alarmRule.getPreRequsite().getEvent().setActivityType(EventType.CREATE.getValue());
			alarmRule.setIsAutoClear(true);
			for (String key : ruleskeys) {
				JSONObject rule = (JSONObject) rulesObj.get(key);
				ObjectMapper mapper = FieldUtil.getMapper(WorkflowContext.class);
				JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray((JSONObject) rule.get("workflow"));
				long thresholdType = (long) rule.get("thresholdType");
				List<WorkflowContext> list = mapper.readValue(JSONArray.toJSONString(fieldJsons), mapper.getTypeFactory().constructCollectionType(List.class, WorkflowContext.class));
				if (((String)rule.get("action")).equals("PreRequsite")) {
					alarmRule.getPreRequsite().setWorkflow(list.get(0));
					FacilioModule preModule = modBean.getModule((String) rule.get("moduleName"));
					FacilioField preRequesitefield = modBean.getField((String) rule.get("threshold_metric"), preModule.getName());
					alarmRule.getPreRequsite().setThresholdType((int) thresholdType);
					alarmRule.getPreRequsite().setReadingFieldId(preRequesitefield.getId());
				} else if (((String)rule.get("action")).equals("TRIGGER_ALARM")) {
					alarmRule.getAlarmTriggerRule().setName("TRIGGER_ALARM");
					alarmRule.getAlarmTriggerRule().setWorkflow(list.get(0));
					alarmRule.getAlarmTriggerRule().setThresholdType((int) thresholdType);
					ActionContext action = new ActionContext();
					action.setActionType(ActionType.ADD_ALARM);
					JSONObject possible = new JSONObject();
					JSONArray fieldMatcher = new JSONArray();
					JSONObject content = new JSONObject();
					content.put("field", "problem");
					content.put("value", obj.get("problem"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "message");
					content.put("value", obj.get("name"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "possibleCauses");
					content.put("value", obj.get("possible_causes"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "severity");
					content.put("value", obj.get("severity"));
					content = new JSONObject();
					fieldMatcher.add(content);
					content.put("field", "recommendation");
					content.put("value", obj.get("possible_solution"));
					fieldMatcher.add(content);
					content = new JSONObject();
					possible.put("fieldMatcher", fieldMatcher);
					action.setTemplateJson(possible);
					alarmRule.getAlarmTriggerRule().setActions(Collections.singletonList(action));
				}
			}
			FacilioContext facilioContext = new FacilioContext();

			facilioContext.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
			Chain addRule = TransactionChainFactory.addAlarmRuleChain();
			addRule.execute(facilioContext);
			
			setResult("rule", alarmRule);
			return SUCCESS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public ReadingRuleContext convertToRuleContext (ReadingRuleContext rule, JSONObject ruleObject) {
		
		return null;
		
	}
	
	public String getDefaultRuleTemplates() throws Exception {
		setResult("templates", TemplateAPI.getAllRuleLibraryTemplate());
		return SUCCESS;
	}
	
	 

}