package com.facilio.bmsconsole.criteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FacilioConditionParser {
	public String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public Criteria criteria;

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	List<FacilioField> fields = null;
	
	public Object getValue() throws Exception {
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			List records = LookupSpecialTypeUtil.getObjects(moduleName, criteria);
			if(records != null && !records.isEmpty()) {
				return records.get(0);
			}
		}
		else {
			Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(moduleName);

			SelectRecordsBuilder<ModuleBaseWithCustomFields> objectValue = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																					.moduleName(moduleName)
																					.select(fields)
																					.beanClass(moduleClass)
																					.andCriteria(criteria);
			List<ModuleBaseWithCustomFields> records = objectValue.get();
			if(records != null && !records.isEmpty()) {
				return records.get(0);
			}
		}
		return null;
	}
	
	public FacilioConditionParser(String templateString) throws Exception {
		int sequence = 0;
		
		String moduleFormatter = "((.*?)\\s*\\[)(?<=\\[)((.*?)`([^`]*)`(.*))(?=\\])";
		Pattern moduleNamePattern = Pattern.compile(moduleFormatter);

		String conditionStringFormatter = "((.*?)`([^`]*)`(.*))";
		Pattern condtionStringpattern = Pattern.compile(conditionStringFormatter);
		StringBuilder sb =  new StringBuilder();
		Map<String, Condition> conditions = new HashMap<>();
		Criteria criteria = new Criteria();
		Matcher moduleMatcher = moduleNamePattern.matcher(templateString);
		String conditionString = null;
		
		while(moduleMatcher.find()){
			moduleName = moduleMatcher.group(2);
			setModuleName(moduleMatcher.group(2));
			conditionString = moduleMatcher.group(3);
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		fieldMap.put("id", FieldFactory.getIdField(module));
		String[] values = conditionString.split(" ");

		for(String value : values) {
			System.out.println(value);
			if(value.equals("&&")) {
				sb.append("AND ");
				System.out.println(sb);
			}
			else if(value.equals("||")) {
				sb.append("OR ");
			}
			else if(value.equals("(")) {
				sb.append("( ");
			}
			else if(value.equals(")")) {
				sb.append(") ");
			}
			else{
				Matcher matcher = condtionStringpattern.matcher(value);
				while (matcher.find()) {
					String fieldName = matcher.group(2);
					Condition condition = new Condition();
					condition.setFieldName(fieldName);
					FacilioField field = null;
					if(fieldName.equals("id")) {
						field = FieldFactory.getIdField(modBean.getModule(moduleName));
					}
					else {
						field = fieldMap.get(fieldName);
					}
					condition.setColumnName(field.getExtendedModule().getTableName()+"."+field.getColumnName());
					condition.setOperator(field.getDataTypeEnum().getOperator(matcher.group(3)));
					condition.setValue(matcher.group(4));
					sequence++;
					sb.append(sequence + " ");
					condition.setSequence(sequence);
					conditions.put(String.valueOf(sequence), condition);
				}
			}
		}
		
		criteria.setConditions(conditions);
		System.out.println(sb);
		criteria.setPattern(sb.toString());
		setCriteria(criteria);
		
	}
				
}
	