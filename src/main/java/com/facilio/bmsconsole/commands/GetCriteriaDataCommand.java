package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.facilio.report.context.ReportUserFilterContext;
import com.facilio.time.DateRange;

public class GetCriteriaDataCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(GetCriteriaDataCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Criteria customCriteria = new Criteria();
				
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if (CollectionUtils.isNotEmpty(report.getUserFilters())) {
			for (ReportUserFilterContext userFilter: report.getUserFilters()) {
				Criteria criteria = userFilter.getCriteria();
				if (criteria != null) {
					customCriteria.andCriteria(criteria);
				}
			}
		}
		customCriteria.andCriteria(report.getDataPoints().get(0).getCriteria());
		
		if(!customCriteria.isEmpty()) {
			String moduleName = (String)context.get("moduleName");
			context.put("criteriaData", getAsJSON(moduleName, customCriteria));
		}
		
		return false;
	}
	
	private JSONObject getAsJSON(String moduleName, Criteria customCriteria) throws Exception{
		JSONObject criteriaObj = new JSONObject();
		JSONArray conditionArray = new JSONArray();
		
		String pattern = customCriteria.getPattern();
		String alias = null;
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, Condition> conditions = customCriteria.getConditions();
		for(Map.Entry<String, Condition> entry : conditions.entrySet()) {
			JSONObject conditionObj = new JSONObject();
			
			Condition condition = entry.getValue();
			
			String conditionValue = condition.getValue();
			JSONArray value = getMetaData(conditionValue);
			
			String[] name = condition.getFieldName().split("\\.");
			String fieldName = name.length > 1 ? name[1] : name[0];
			FacilioField field = modBean.getField(fieldName, moduleName);
			String fieldType = FieldType.getCFType(field.getDataType()).getTypeAsString();
			alias = getAlias(alias);
			pattern = pattern.replace(entry.getKey(), alias);
			conditionObj.put("key", alias);
			conditionObj.put("field", field.getDisplayName());
			conditionObj.put("fieldType", fieldType);
			conditionObj.put("operator", condition.getOperator().getOperator());
			if(condition.getOperator() instanceof DateOperators){
				handleDateOperators(conditionObj, condition);
			}else{
				if (field.getDataTypeEnum()==FieldType.LOOKUP) {
					List<Long> fieldIds = value;
					if(CollectionUtils.isNotEmpty(fieldIds)){
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
				else if(fieldName.equals("siteId")){
					List<Long> fieldIds = value;
					conditionObj.put("value", CommonCommandUtil.getPickList(fieldIds, modBean.getModule("Site")).values());
				}
//				else if(fieldName.equals("sourceType")){
//					conditionObj.put("value", SourceType.getType((int) value.get(0)).getStringVal());
//				}
				else{
					conditionObj.put("value", value);
				}
			}
			
			conditionArray.add(conditionObj);
		}
		criteriaObj.put("conditions", conditionArray);
		criteriaObj.put("pattern", pattern);
		
		return criteriaObj;
	}
	private JSONArray getMetaData(String conditionValue) throws Exception{
		JSONArray value = new JSONArray();
		if(conditionValue != null) {
			String[] values = conditionValue.trim().split("\\s*,\\s*");
			for(String val : values) {
				if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
					val = String.valueOf(AccountUtil.getCurrentUser().getId());
					value.add(val);
				}
				else if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
					List<Long> ids = getLoggedInUserGroupIds();
					for(Long id: ids) {
						value.add(id);
					}
				}
				else {
					value.add(val);
				}
			}
		}
		return value;
	}
	private void handleDateOperators(JSONObject conditionObj, Condition condition){
		DateOperators operator = (DateOperators)condition.getOperator();
		DateRange range = operator.getRange(condition.getValue());
		conditionObj.put("operator", "Between");
		JSONArray rangeArray = new JSONArray();
		rangeArray.add(range.getStartTime());
		rangeArray.add(range.getEndTime());
		conditionObj.put("value", rangeArray);
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
	
	private String getAlias(String previous){
		String alias = "A";
		if(StringUtils.isAlpha(previous)){
			int preIndex = previous.length()-1;
			char preChar = previous.charAt(preIndex);
			if(preChar == 'Z'){
				previous = previous.substring(0, preIndex);
				if(StringUtils.isNotEmpty(previous)){
					alias = getAlias(previous)+alias;
				}else{
            	    alias+=alias;				    
				}
			}else{
				alias = previous.substring(0, preIndex)+Character.toString(++preChar);
			}
		}
		return alias;
	}
}