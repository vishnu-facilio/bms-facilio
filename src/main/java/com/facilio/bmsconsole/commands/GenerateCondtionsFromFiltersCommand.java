package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenerateCondtionsFromFiltersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		if(filters != null && !filters.isEmpty()) {
			List<Condition> conditionList = new ArrayList<>();
			Iterator<String> filterIterator = filters.keySet().iterator();
			while(filterIterator.hasNext())
			{
				String fieldName = filterIterator.next();
				Object fieldJson = filters.get(fieldName);
				if(fieldJson!=null && fieldJson instanceof JSONArray) {
					JSONArray fieldJsonArr = (JSONArray) fieldJson;
					for(int i=0;i<fieldJsonArr.size();i++) {
						JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
						setCondition(fieldName, fieldJsonObj, conditionList);
					}
				}
				else if(fieldJson!=null && fieldJson instanceof JSONObject) {
					JSONObject fieldJsonObj = (JSONObject) fieldJson;
					setCondition(fieldName, fieldJsonObj, conditionList);
				}
			}
			context.put(FacilioConstants.ContextNames.FILTER_CONDITIONS, conditionList);
		}
		return false;
	}
	private void setCondition(String fieldName, JSONObject fieldJson, List<Condition> conditionList) throws Exception {
		JSONArray value = (JSONArray) fieldJson.get("value");
		if((value!=null && value.size() > 0) || (fieldJson.get("operator") != null && !((String) fieldJson.get("operator")).equals("is")))
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String[] module = ((String) fieldJson.get("module")).split("\\.");
			if(module.length > 1)
			{
				FacilioField field = modBean.getField(module[1], module[0]);
				Condition condition = new Condition();
				condition.setField(field);
				condition.setOperator(LookupOperator.LOOKUP);
				
				FacilioField childField = modBean.getField(fieldName, module[1]);
				Condition childCondition = new Condition();
				childCondition.setField(childField);
				childCondition.setOperator(childField.getDataTypeEnum().getOperator((String) fieldJson.get("operator")));
				
				if(value.size() > 0)
				{
					StringBuilder values = new StringBuilder();
					boolean isFirst = true;
					Iterator<String> iterator = value.iterator();
					while(iterator.hasNext())
					{
						String obj = iterator.next();
						if(!isFirst)
						{
							values.append(",");
						}
						else
						{
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
					childCondition.setValue(values.toString());
				}
				
				Criteria criteria = new Criteria();
				criteria.addAndCondition(childCondition);
				
				condition.setCriteriaValue(criteria);
				conditionList.add(condition);
			}
			else
			{
				FacilioField field = modBean.getField(fieldName, module[0]);
				Condition condition = new Condition();
				condition.setField(field);
				condition.setOperator(field.getDataTypeEnum().getOperator((String) fieldJson.get("operator")));
				
				if(value!=null && value.size()>0) {
					StringBuilder values = new StringBuilder();
					boolean isFirst = true;
					Iterator<String> iterator = value.iterator();
					while(iterator.hasNext())
					{
						String obj = iterator.next();
						if(!isFirst)
						{
							values.append(",");
						}
						else
						{
							isFirst = false;
						}
						values.append(obj.substring(0, obj.indexOf("_")));
					}
					condition.setValue(values.toString());
				}
				conditionList.add(condition);
			}
		}
	}

}
