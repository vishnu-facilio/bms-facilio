package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenerateCriteriaFromFilterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		if(filters != null && !filters.isEmpty()) {
			
			Iterator<String> filterIterator = filters.keySet().iterator();
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			Criteria criteria = new Criteria();
			while(filterIterator.hasNext())
			{
				String fieldName = filterIterator.next();
				Object fieldJson = filters.get(fieldName);
				List<Condition> conditionList = new ArrayList<>();
				if(fieldJson!=null && fieldJson instanceof JSONArray) {
					JSONArray fieldJsonArr = (JSONArray) fieldJson;
					for(int i=0;i<fieldJsonArr.size();i++) {
						JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
						setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
					}
				}
				else if(fieldJson!=null && fieldJson instanceof JSONObject) {
					JSONObject fieldJsonObj = (JSONObject) fieldJson;
					setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
				}
				criteria.groupOrConditions(conditionList);
			}
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
		}
		return false;
	}
	
	private void setConditions(String moduleName, String fieldName, JSONObject fieldJson,List<Condition> conditionList) throws Exception {
		
		Object operatorIdObj = fieldJson.get("operatorId");
		if(operatorIdObj instanceof JSONArray) {
			JSONArray operatorArray = (JSONArray) operatorIdObj;
			for(int i=0;i<operatorArray.size();i++) {
				long operatorId = (long) operatorArray.get(i);
				setCondition(moduleName, fieldName, (int)operatorId, null, conditionList);
			}
		}
		else {
			JSONArray value = (JSONArray) fieldJson.get("value");
			int operatorId = (int) (long)operatorIdObj;
			String operatorName = Operator.OPERATOR_MAP.get(operatorId).getOperator();
			if((value!=null && value.size() > 0) || (operatorIdObj != null && !(operatorName.equals("is")) ) ) {
				setCondition(moduleName, fieldName,operatorId, value, conditionList);
			}
		}
	}

	private void setCondition(String moduleName,String fieldName,int operatorId, JSONArray value, List<Condition> conditionList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(fieldName, moduleName);
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperatorId(operatorId);
		
		if(value!=null && value.size()>0) {
			StringBuilder values = new StringBuilder();
			boolean isFirst = true;
			Iterator<String> iterator = value.iterator();
			while(iterator.hasNext())
			{
				String obj = iterator.next();
				if(!isFirst) {
					values.append(",");
				}
				else {
					isFirst = false;
				}
				if (obj.indexOf("_") != -1) {
					try {
						long id = Long.parseLong(obj.split("_")[0]);
						values.append(id+"");
					}
					catch (Exception e) {
						values.append(obj);
					}
				}
				else {
					values.append(obj);
				}
			}
			condition.setValue(values.toString());
		}
		conditionList.add(condition);
	}

}
