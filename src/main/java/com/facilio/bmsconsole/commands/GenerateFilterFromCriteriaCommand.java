package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.LookupOperator;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateFilterFromCriteriaCommand implements Command{

	private static Logger log = LogManager.getLogger(GenerateFilterFromCriteriaCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(view != null) {
			Criteria criteria = view.getCriteria();
			if(criteria != null) {
				JSONObject filters = new JSONObject();
				setFilters(criteria,filters);
				view.setFilters(filters);
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void setFilters(Criteria criteria, JSONObject filters) throws Exception {
		Map<String, Condition> conditions = criteria.getConditions();
		for(Map.Entry<String, Condition> entry : conditions.entrySet()) {
			Condition condition = entry.getValue();
			String[] name = condition.getFieldName().split("\\.");
			String fieldName = name.length > 1 ? name[1] : name[0];
			JSONObject filter = new JSONObject();
			filter.put("operatorId", condition.getOperatorId());
			String conditionValue = condition.getValue();
			Object value = null;
			if(conditionValue != null) {
				value = new JSONArray();
				String[] values = conditionValue.trim().split("\\s*,\\s*");
				for(String val : values) {
					if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
						val = String.valueOf(AccountUtil.getCurrentUser().getId());
						((JSONArray) value).add(val);
					}
					else if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
						List<Long> ids = getLoggedInUserGroupIds();
						for(Long id: ids) {
							((JSONArray) value).add(id);
						}
					}
					else {
						((JSONArray) value).add(val);
					}
				}
			}
			else if(condition.getOperatorId() == LookupOperator.LOOKUP.getOperatorId()) {
				if(condition.getCriteriaValue() != null) {
					value = new JSONObject();
					setFilters(condition.getCriteriaValue(),(JSONObject)value);
				}
				else if(condition.getCriteriaValueId() != -1) {
					value = new JSONObject();
					Criteria criteriaValue = CriteriaAPI.getCriteria(criteria.getOrgId(),condition.getCriteriaValueId());
					setFilters(criteriaValue,(JSONObject)value);
				}
			}
			filter.put("value",value);
			if(filters.containsKey(fieldName)) {
				Object fieldFilter = filters.get(fieldName);
				if(!(fieldFilter instanceof JSONArray)) {
					fieldFilter = new JSONArray();
					((JSONArray)fieldFilter).add(filters.get(fieldName));
				}
				((JSONArray)fieldFilter).add(filter);
				filters.put(fieldName, fieldFilter);
			}
			else {
				filters.put(fieldName, filter);
			}
		}
	}
	
	private static List<Long> getLoggedInUserGroupIds () {
		List<Long> objs = new ArrayList<Long>();
		try {
			List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
			if (myGroups != null && !myGroups.isEmpty()) {
				objs = myGroups.stream().map(Group::getId).collect(Collectors.toList());
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return objs;
	}

}
