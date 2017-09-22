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
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
				JSONObject fieldJson = (JSONObject) filters.get(fieldName);
				JSONArray value = (JSONArray) fieldJson.get("value");
				if(value.size() > 0 || (fieldJson.get("operator") != null && !((String) fieldJson.get("operator")).equals("is")))
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
						childCondition.setOperator(childField.getDataType().getOperator((String) fieldJson.get("operator")));
						
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
									values.append(obj.substring(0, obj.indexOf("_")));
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
						condition.setOperator(field.getDataType().getOperator((String) fieldJson.get("operator")));
						
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
						conditionList.add(condition);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.FILTER_CONDITIONS, conditionList);
		}
		return false;
	}

}
