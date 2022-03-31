package com.facilio.qa.displaylogic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicAction;
import com.facilio.qa.displaylogic.context.DisplayLogicActionType;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;

public class DisplayLogicUtil {
	
	public static final String DISPLAY_LOGIC_CONTEXT = "displayLogicContext";
	public static final String DISPLAY_LOGIC_ACTION_CONTEXT = "displayLogicActionContext";
	public static final String DISPLAY_LOGIC_CONTEXTS = "displayLogicContexts";
	public static final String ANSWER_MAP = "answerMap";
	public static final String TRIGGER_QUESTION_IDS = "triggerQuestionIds";
	public static final String QUESTION_ID = "questionId";
	public static final String PAGE_ID = "pageId";
	public static final String JSON_RESULT_VALUE_STRING = "value";
	public static final String JSON_RESULT_ACTION_NAME_STRING = "action";
	public static final String JSON_RESULT_QUESTION_ID_STRING = "questionId";
	public static final String DISPLAY_LOGIC_RULE_RESULT_JSON = "resultJsonArray";
	

	public static JSONObject getActionJson(Long questionId,DisplayLogicActionType actionType,Object value) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(JSON_RESULT_QUESTION_ID_STRING,questionId);
		jsonObject.put(JSON_RESULT_ACTION_NAME_STRING,actionType.getName());
		jsonObject.put(JSON_RESULT_VALUE_STRING,value);
		
		return jsonObject;
	}
	
	public static List<DisplayLogicTriggerQuestions> fetchDisplayLogicTriggerQuestion(List<Long> questionIDs) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getQAndADisplayLogicTriggerQuestionFields())
				.table(ModuleFactory.getQAndADisplayLogicTriggerQuestionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicTriggerQuestionFields(), "triggerQuestionId"), questionIDs, NumberOperators.EQUALS))
				;
		
		return FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), DisplayLogicTriggerQuestions.class);
	}
	
	public static List<DisplayLogicContext> fetchDisplayLogic(Condition condition) throws Exception {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		return fetchDisplayLogic(criteria);
	}
	
	public static List<DisplayLogicContext> fetchDisplayLogic(Criteria criteria) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getQAndADisplayLogicFields())
				.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
				.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<DisplayLogicContext> displayLogicContexts = new ArrayList<DisplayLogicContext>();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				DisplayLogicContext displayLogic = FieldUtil.getAsBeanFromMap(prop, DisplayLogicContext.class);
				if(displayLogic.getCriteriaId() != null) {
					displayLogic.setCriteria(CriteriaAPI.getCriteria(displayLogic.getCriteriaId()));
				}
				displayLogicContexts.add(displayLogic);
			}
			List<Long> ids = displayLogicContexts.stream().map(DisplayLogicContext::getId).collect(Collectors.toList());
			Map<Long, List<DisplayLogicTriggerQuestions>> triggerMap = fetchTriggerQuestions(ids);
			Map<Long, List<DisplayLogicAction>> actionMap = fetchActions(ids);
			
			for(DisplayLogicContext displayLogicContext : displayLogicContexts) {
				displayLogicContext.setTriggerQuestions(triggerMap.get(displayLogicContext.getId()));
				displayLogicContext.setActions(actionMap.get(displayLogicContext.getId()));
			}
			
			return displayLogicContexts;
		}
		return null;
	}


	private static Map<Long, List<DisplayLogicAction>> fetchActions(List<Long> logicIds) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getQAndADisplayLogicActionFields())
				.table(ModuleFactory.getQAndADisplayLogicActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicActionFields(), "displayLogicId"), logicIds, NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<DisplayLogicAction> actions = FieldUtil.getAsBeanListFromMapList(props, DisplayLogicAction.class);
		if(actions.isEmpty()) {
			return null;
		}
		return actions.stream().collect(Collectors.groupingBy(DisplayLogicAction::getDisplayLogicId));
	}


	private static Map<Long, List<DisplayLogicTriggerQuestions>> fetchTriggerQuestions(List<Long> logicIds) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getQAndADisplayLogicTriggerQuestionFields())
				.table(ModuleFactory.getQAndADisplayLogicTriggerQuestionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicTriggerQuestionFields(), "displayLogicId"), logicIds, NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<DisplayLogicTriggerQuestions> triggerQuestion = FieldUtil.getAsBeanListFromMapList(props, DisplayLogicTriggerQuestions.class);
		if(triggerQuestion.isEmpty()) {
			return null;
		}
		return triggerQuestion.stream().collect(Collectors.groupingBy(DisplayLogicTriggerQuestions::getDisplayLogicId));
	}
}