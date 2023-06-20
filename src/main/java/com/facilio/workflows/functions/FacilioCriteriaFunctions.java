package com.facilio.workflows.functions;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import org.apache.commons.collections.Predicate;
import org.json.simple.JSONObject;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CRITERIA_FUNCTION)
public class FacilioCriteriaFunctions {
	public Object get(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		Criteria criteria = null;
		if(objects[0] instanceof JSONObject) {
			JSONObject criteriaJson = (JSONObject)objects[0];
			criteria = FieldUtil.getAsBeanFromJson(criteriaJson, Criteria.class);
		}
		else if(objects[0] instanceof Map) {
			Map criteriaMap = (Map)objects[0];
			criteria = FieldUtil.getAsBeanFromMap(criteriaMap, Criteria.class);
		}

		return criteria;
	}

	public Object evaluate(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		Criteria criteria = (Criteria)objects[0];

		Map<String, Object> data = (Map<String, Object>)objects[1];

		Predicate Predicate = criteria.computePredicate(data);
		if(Predicate.evaluate(data)) {
			return true;
		}
		return false;
	}

	public Object and(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		Criteria criteria = (Criteria)objects[0];

		Criteria criteria1 = (Criteria)objects[1];

		criteria.andCriteria(criteria1);
		return criteria;
	}

	public Object or(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		Criteria criteria = (Criteria)objects[0];

		Criteria criteria1 = (Criteria)objects[1];

		criteria.orCriteria(criteria1);
		return criteria;
	}

	public Object getContainsCriteria(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		String fieldName = (String)objects[0];
		String value = (String)objects[1];

		Condition condition = CriteriaAPI.getCondition(fieldName, value, StringOperators.CONTAINS);

		Criteria criteria = new Criteria();

		criteria.addAndCondition(condition);

		return criteria;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}