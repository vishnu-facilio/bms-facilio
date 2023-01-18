package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.db.criteria.operators.*;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

public class GenerateCriteriaFromFilterCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GenerateCriteriaFromFilterCommand.class.getName());
	private List<String> templateFields;
	boolean isPm;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		boolean isV4 = Constants.isV4(context);
		if (isV4) {
			return false;
		}
		long startTime = System.currentTimeMillis();
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		if(filters != null && !filters.isEmpty()) {
			
			Iterator<String> filterIterator = filters.keySet().iterator();
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			isPm = moduleName.equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			if (isPm) {
				templateFields = PreventiveMaintenanceAPI.getTemplateFields();
			}

			String drillDownPattern=null;
			if(filters.containsKey(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN) && filters.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN) != null) {
				drillDownPattern = filters.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN).toString();
				filters.remove(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_PATTERN);
			}

			Criteria criteria = new Criteria();

			if(StringUtils.isNotEmpty(drillDownPattern)){
				Queue<String> fieldNamesFromFilter = getFieldNamesFromFilter(filters);
				filterIterator = fieldNamesFromFilter.iterator();
			}

			while(filterIterator.hasNext())
			{
				String fieldName = filterIterator.next();
				Object fieldJson = filters.get(fieldName);

				if(StringUtils.isNotEmpty(drillDownPattern)){
					fieldName = fieldName.split("----")[0];
				}

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

			if(StringUtils.isNotEmpty(drillDownPattern)){
				criteria.setPattern(drillDownPattern);
			}
			if (!criteria.isEmpty()) {
				context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute GenerateCriteriaFromFilterCommand : "+timeTaken);
		return false;
	}

	private boolean isScopeFieldAndNonScopeModule (String fieldName, String moduleName, ModuleBean modBean) throws Exception {
		switch (fieldName) {
			case "site":
			case "siteId":
				return !FieldUtil.isSiteIdFieldPresent(modBean.getModule(moduleName), true);
		}
		return false;
	}
	public Queue<String> getFieldNamesFromFilter(JSONObject filter){
		Queue<String> filedNamesQueue = new LinkedList<>();
		for (long index = 1; index <= filter.size(); index++){
			String fieldName = getFieldNameById(index, filter);
			filedNamesQueue.add(fieldName);
		}
		return filedNamesQueue;
	}
	public String getFieldNameById(long id, JSONObject filter){
		for (Object fieldName: filter.keySet()) {
			JSONObject jsonObject = (JSONObject) filter.get(fieldName);
			long fieldId = (Long) jsonObject.get("id");
			if(fieldId == id) return fieldName.toString();
		}
		return null;
	}
	private void setConditions(String moduleName, String fieldName, JSONObject fieldJson,List<Condition> conditionList) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(isPm) {
			moduleName = PreventiveMaintenanceAPI.getPmModule(templateFields, fieldName);
		}
		
		int operatorId = -1;
		String operatorName;

		Operator operator = null ;
		if (fieldJson.containsKey("operatorId")) {
			operatorId = (int) (long) fieldJson.get("operatorId");
			operator = Operator.getOperator(operatorId);
		}

		if(operator != null && operator instanceof RelationshipOperator) {
			Condition condition = new Condition();
			condition.setFieldName(fieldName);
			condition.setOperatorId(operatorId);

			JSONArray value = (JSONArray) fieldJson.get("value");

			if(operator.isValueNeeded() && (value!=null && value.size()>0)) {
				StringBuilder values = new StringBuilder();
				boolean isFirst = true;
				Iterator<String> iterator = value.iterator();
				while (iterator.hasNext()) {
					String obj = iterator.next();
					if (!isFirst) {
						values.append(",");
					} else {
						isFirst = false;
					}
					values.append(obj);
				}
				condition.setValue(values.toString());
				condition.validateValue();
			}
			conditionList.add(condition);
			return;
		}


		boolean isOldField = false;
		//TODO Remove the check once handled in mobile
		if (OLD_FIELDS_MODULE.contains(moduleName) && OLD_FIELDS.contains(fieldName)) {
			fieldName = fieldName.replace("Id", "");
			isOldField = true;
		}
		
		String relatedFieldName = (String) fieldJson.get("relatedFieldName");
		FacilioField field;
		if(relatedFieldName != null) {
			// here fieldName is modulename of subquery
			field = modBean.getField(relatedFieldName, fieldName);
		}
		else {
			field = modBean.getField(fieldName, moduleName);
		}
		if (field == null) {
			// Temp handling...needs to check
			if (fieldName.equals("site") && OLD_FIELDS_MODULE.contains(moduleName)) {
				field = modBean.getField("siteId", moduleName);
			}
			if (field == null) {
				if (isScopeFieldAndNonScopeModule(fieldName, moduleName, modBean)) {
					return;
				}
				else {
					LOGGER.error("Field is not found for: " + fieldName + " : " + moduleName);
					throw new IllegalArgumentException("Field is not found for: " + fieldName + " : " + moduleName);
				}
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
			operator = Operator.getOperator(operatorId);
			operatorName = operator.getOperator();
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
			operator = field.getDataTypeEnum().getOperator(operatorName);
			operatorId = operator.getOperatorId();
		}
		JSONArray value = (JSONArray) fieldJson.get("value");

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperatorId(operatorId);

		if(operator.isValueNeeded() && (value!=null && value.size()>0)) {
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
				if (operator instanceof StringOperators || operator instanceof StringSystemEnumOperators || operator instanceof UrlOperators) {
					obj = obj.replace(",", StringOperators.DELIMITED_COMMA);
					values.append(obj.trim());
				} else {
					String splDisplayName = PickListOperators.getDisplayNameForCurrentValue(obj);
					if (StringUtils.isEmpty(splDisplayName)) {
						values.append(ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), obj.trim()));
					}
					else {
						values.append(obj.trim());
					}
				}
			}
			String valuesString = values.toString();
			if (condition.getOperator() instanceof FieldOperator) {
				condition.setValueField(modBean.getField(valuesString, moduleName));
			} else if(condition.getOperator() instanceof RelatedModuleOperator) {
				Criteria criteriaVal = new Criteria();
				Operator relatedOperator = NumberOperators.EQUALS;
				if (fieldJson.containsKey("relatedOperatorId")) {
					relatedOperator = Operator.getOperator((int)(long) fieldJson.get("relatedOperatorId"));
				}
				FacilioField relatedField;
				if (fieldJson.containsKey("filterFieldName")) {
					relatedField = modBean.getField((String)fieldJson.get("filterFieldName"), fieldName);
				}
				else {
					relatedField = FieldFactory.getIdField(modBean.getModule(fieldName));
				}
				criteriaVal.addAndCondition(CriteriaAPI.getCondition(relatedField, valuesString, relatedOperator));
				condition.setCriteriaValue(criteriaVal);
			} else if (condition.getOperator() instanceof LookupOperator) {
				Criteria criteriaVal = new Criteria();
				Operator lookupOperator = Operator.getOperator((int)(long) fieldJson.get("lookupOperatorId"));
				FacilioField lookupFilterField = modBean.getField((String)fieldJson.get("field"), ((LookupField)field).getLookupModule().getName());
				criteriaVal.addAndCondition(CriteriaAPI.getCondition(lookupFilterField, valuesString, lookupOperator));
				condition.setCriteriaValue(criteriaVal);
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
		condition.validateValue();
		conditionList.add(condition);
	}
	
	private static final List<String> OLD_FIELDS = Arrays.asList("siteId", "spaceId", "buildingId", "floorId", "spaceId1", "spaceId2", "spaceId3", "spaceId4");
	private static final List<String> OLD_FIELDS_MODULE = Arrays.asList(FacilioConstants.ContextNames.BASE_SPACE, FacilioConstants.ContextNames.RESOURCE, FacilioConstants.ContextNames.SPACE, FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.BUILDING, FacilioConstants.ContextNames.FLOOR);
	

}
