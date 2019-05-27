package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.FieldOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GenerateCriteriaFromFilterCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(GenerateCriteriaFromFilterCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();
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
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute GenerateCriteriaFromFilterCommand : "+timeTaken);
		return false;
	}
	
	private void setConditions(String moduleName, String fieldName, JSONObject fieldJson,List<Condition> conditionList) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(moduleName.equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE)) {
			moduleName = gePreventiveMaintenanceModule(fieldName);
		}
		
		int operatorId;
		String operatorName;
		
		boolean isOldField = false;
		//TODO Remove the check once handled in mobile
		if (OLD_FIELDS_MODULE.contains(moduleName) && OLD_FIELDS.contains(fieldName)) {
			fieldName = fieldName.replace("Id", "");
			isOldField = true;
		}
		
		FacilioField field = modBean.getField(fieldName, moduleName);
		if (field == null) {
			// Temp handling...needs to check
			if (fieldName.equals("site") && OLD_FIELDS_MODULE.contains(moduleName)) {
				field = modBean.getField("siteId", moduleName);
			}
			if (field == null) {
				LOGGER.error("Field is not found for: " + fieldName + " : " + moduleName);
				throw new Exception("Field is not found for: " + fieldName + " : " + moduleName);
			}
		}
		
		if (fieldJson.containsKey("operatorId")) {
			operatorId = (int) (long) fieldJson.get("operatorId");
			if (isOldField) {
				if (operatorId == NumberOperators.EQUALS.getOperatorId()) {
					operatorId = PickListOperators.IS.getOperatorId();
				}
				else if (operatorId == NumberOperators.NOT_EQUALS.getOperatorId()) {
					operatorId = PickListOperators.ISN_T.getOperatorId();
				}
			}
			operatorName = Operator.OPERATOR_MAP.get(operatorId).getOperator();
		} else {
			operatorName = (String) fieldJson.get("operator");
			if (isOldField) {
				if (operatorName.equals(NumberOperators.EQUALS.getOperator())) {
					operatorName = PickListOperators.IS.getOperator();
				}
				else if (operatorName.equals(NumberOperators.NOT_EQUALS.getOperator())) {
					operatorName = PickListOperators.ISN_T.getOperator();
				}
			}
			operatorId = field.getDataTypeEnum().getOperator(operatorName).getOperatorId();
		}
		JSONArray value = (JSONArray) fieldJson.get("value");
		if((value!=null && value.size() > 0) || (operatorName != null && !(operatorName.equals("is")) ) ) {
			
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
					if (obj.indexOf("_") != -1 && !obj.equals(FacilioConstants.Criteria.LOGGED_IN_USER) && !obj.equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
						try {
							String filterValue = obj.split("_")[0];
							values.append(filterValue);
						}
						catch (Exception e) {
							values.append(obj);
						}
					}
					else {
						values.append(obj.trim());
					}
				}
				String valuesString = values.toString();
				if (condition.getOperator() instanceof FieldOperator) {
					condition.setValueField(modBean.getField(valuesString, moduleName));
				} else {
					condition.setValue(valuesString);
				}
				if (fieldJson.containsKey("orFilters")) {	// To have or condition for different fields..eg: (space=1 OR purposeSpace=1)
					JSONArray orFilters = (JSONArray) fieldJson.get("orFilters");
					for(int i=0;i<orFilters.size();i++) {
						JSONObject fieldJsonObj = (JSONObject) orFilters.get(i);
						if (!fieldJsonObj.containsKey("operator")) {
							fieldJsonObj.put("operator", operatorName);
						}
						if (!fieldJsonObj.containsKey("value")) {
							fieldJsonObj.put("value", value);
						}
						setConditions(moduleName, (String)fieldJsonObj.get("field"), fieldJsonObj, conditionList);
					}
				}
			}
			conditionList.add(condition);
		}
	}
	
	private static final List<String> TEMPLATE_FIELDS = Arrays.asList("priorityId", "statusId", "categoryId", "typeId", "assignmentGroupId", "assignedToId", "resourceId", "tenantId", "additionInfo");
	private static final List<String> OLD_FIELDS = Arrays.asList("siteId", "spaceId", "buildingId", "floorId", "spaceId1", "spaceId2", "spaceId3", "spaceId4");
	private static final List<String> OLD_FIELDS_MODULE = Arrays.asList(FacilioConstants.ContextNames.BASE_SPACE, FacilioConstants.ContextNames.RESOURCE, FacilioConstants.ContextNames.SPACE, FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.BUILDING, FacilioConstants.ContextNames.FLOOR);
	private String gePreventiveMaintenanceModule(String fieldName) {
		if (TEMPLATE_FIELDS.contains(fieldName)) {
			return FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE;
		}
		return FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE;
	}

}
