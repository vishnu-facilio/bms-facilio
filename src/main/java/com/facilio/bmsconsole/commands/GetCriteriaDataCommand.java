package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.time.DateRange;

public class GetCriteriaDataCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(GetCriteriaDataCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		List<ReportDataPointContext> dataPoints = new ArrayList<>(report.getDataPoints());
		Criteria customCriteria = dataPoints.get(0).getCriteria();
		if(customCriteria != null && !customCriteria.isEmpty()) {
			String moduleName = (String)context.get("moduleName");
			context.put("criteriaData", getAsJSON(moduleName, customCriteria));
		}
		
		return false;
	}
	private JSONObject getAsJSON(String moduleName, Criteria customCriteria) throws Exception{
		JSONObject criteriaObj = new JSONObject();
		JSONArray conditionArray = new JSONArray();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, Condition> conditions = customCriteria.getConditions();
		for(Map.Entry<String, Condition> entry : conditions.entrySet()) {
			JSONObject conditionObj = new JSONObject();
			
			Condition condition = entry.getValue();
			
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
			String[] name = condition.getFieldName().split("\\.");
			String fieldName = name.length > 1 ? name[1] : name[0];
			FacilioField field = modBean.getField(fieldName, moduleName);
			String fieldType = FieldType.getCFType(field.getDataType()).getTypeAsString();
			conditionObj.put("key", entry.getKey());
			conditionObj.put("field", fieldName);
			conditionObj.put("fieldType", fieldType);
			conditionObj.put("operator", condition.getOperator());
			if(condition.getOperator() instanceof DateOperators){
				DateOperators operator = (DateOperators)condition.getOperator();
				DateRange range = operator.getRange(condition.getValue());
				conditionObj.put("operator", "Between");
				JSONArray rangeArray = new JSONArray();
				rangeArray.add(range.getStartTime());
				rangeArray.add(range.getEndTime());
				conditionObj.put("value", rangeArray);
			}else{
				if (field.getDataTypeEnum()==FieldType.LOOKUP) {
					List<Long> fieldIds = (List<Long>)value;

					LookupField lookupField= (LookupField)field;
					String fieldModuleName = lookupField.getSpecialType();
					if(!LookupSpecialTypeUtil.isSpecialType((fieldModuleName))){
						fieldModuleName = lookupField.getLookupModule().getName();
					}
					Map<Long,Object> idMap = null;
					if(LookupSpecialTypeUtil.isSpecialType(fieldModuleName)) {
						idMap = LookupSpecialTypeUtil.getPickList(fieldModuleName,fieldIds);
					}
					else {
						idMap = CommonCommandUtil.getPickList(fieldIds, modBean.getModule(fieldModuleName));
					}
					if(idMap!=null) {
						conditionObj.put("value",idMap.values());
					}
				}else{
					conditionObj.put("value", value);
				}
			}
			
			conditionArray.add(conditionObj);
		}
		criteriaObj.put("conditions", conditionArray);
		criteriaObj.put("pattern", customCriteria.getPattern());
		return criteriaObj;
	}
//	private JSONObject getMetaData(String moduleName, Criteria customCriteria) throws Exception{
//		
//	}
//	private JSONObject getRawData(JSONObject metaData){
//		JSONObject rawData = new JSONObject();
//		return rawData;
//	}
	
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