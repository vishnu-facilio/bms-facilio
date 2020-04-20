package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class AddFieldsCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		//Have to be converted to batch insert
		List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);
		if(modules != null && !modules.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> fieldIds = new ArrayList<>();
			List<List<ReadingRuleContext>> readingRules = new ArrayList<>();
			List<List<List<ActionContext>>> actionsList = new ArrayList<>();
			Boolean allowSameName = (Boolean) context.get(ContextNames.ALLOW_SAME_FIELD_DISPLAY_NAME);
			if (allowSameName == null) {
				allowSameName = false;
			}
			Boolean isNewModules = (Boolean) context.get(FacilioConstants.ContextNames.IS_NEW_MODULES);
			if (isNewModules == null) {
				isNewModules = false;
			}
			
			
			for (FacilioModule module : modules) {
				FacilioModule cloneMod = new FacilioModule(module);
				if(module != null && module.getFields() != null && !module.getFields().isEmpty()) {
					
					List<FacilioField> existingFields = isNewModules ? null : modBean.getAllFields(module.getName());
					Map<FieldType, List<String>> existingColumns = getColumnNamesGroupedByType(existingFields);
					
					List<FacilioField> counterFields = new ArrayList<>();
					
					for(FacilioField field : module.getFields()) {
						
						setColumnName(field, existingColumns);
						
						field.setModule(cloneMod);
						constructFieldName(field, module, allowSameName);
						long fieldId = modBean.addField(field);
						field.setFieldId(fieldId);
						fieldIds.add(fieldId);
						if (field instanceof NumberField) {
							NumberField numberField = (NumberField) field;
							if (numberField.isCounterField()) {
								NumberField deltaField = FieldUtil.cloneBean(numberField, NumberField.class);
								deltaField.setCounterField(null);
								deltaField.setId(-1);
								
								deltaField.setColumnName(null);
								setColumnName(deltaField, existingColumns);
								
								deltaField.setName(deltaField.getName()+"Delta");
								deltaField.setDisplayName(deltaField.getDisplayName()+" Delta");
								
								long deletaFieldId = modBean.addField(deltaField);
								deltaField.setFieldId(deletaFieldId);
								fieldIds.add(deletaFieldId);
								
								counterFields.add(deltaField);
							}
						}
					}
					
					if (!counterFields.isEmpty()) {
						module.getFields().addAll(counterFields);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
			context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
		}
		else {
			Boolean suppressException = (Boolean) context.get("should_suppress_exception");
			if (suppressException == null || !suppressException) {
				throw new IllegalArgumentException("No Fields to Add");
			}
		}
		return false;
	}
	
	private void setColumnName(FacilioField field, Map<FieldType, List<String>> existingColumns) throws Exception {
		FieldType dataType = field.getDataTypeEnum();
		if(dataType != null) {
			List<String> existingColumnNames = existingColumns.get(dataType);
			if(existingColumnNames == null) {
				existingColumnNames = new ArrayList<>();
				existingColumns.put(dataType, existingColumnNames);
			}
			if(field.getColumnName() == null || field.getColumnName().isEmpty()) {
				String newColumnName = getColumnNameForNewField(dataType, existingColumnNames);
				if(newColumnName == null) {
					throw new Exception("No more columns available.");
				}
				field.setColumnName(newColumnName);
			}
			existingColumnNames.add(field.getColumnName());
		}
		else {
			throw new IllegalArgumentException("Invalid Data Type Value");
		}
	}
	
	private void constructFieldName(FacilioField field, FacilioModule module, boolean changeDisplayName) throws Exception {
		if(field.getName() == null || field.getName().isEmpty()) {
			if(field.getDisplayName() != null && !field.getDisplayName().isEmpty()) {
				field.setName(field.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
				// Will be used while adding a new field from form
				if (changeDisplayName) {
					changeDisplayName(field);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid name for field of module : "+module.getName());
			}
		}
	}
	

	private void changeDisplayName(FacilioField field) throws Exception {
		FacilioModule module = ModuleFactory.getFieldsModule();
		List<FacilioField> fields = FieldFactory.getSelectFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(field.getModuleId()), NumberOperators.EQUALS))
					.orderBy(fieldMap.get("fieldId").getColumnName() + " desc")
					.limit(1);
		
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("name"), field.getName(), StringOperators.IS));
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("name"), field.getName() + "\\_%", StringOperators.CONTAINS));
		builder.andCriteria(criteria);
		
		List<Map<String, Object>> props = builder.get();
		String dbFieldName = null;
		if (props != null && !props.isEmpty()) {
			dbFieldName = (String) props.get(0).get("name");
			int count = 0;
			if (dbFieldName.contains("_")) {
				count = Integer.parseInt(dbFieldName.substring(dbFieldName.lastIndexOf('_') + 1));
			}
			field.setName(field.getName() + "_" + ++count);
			field.setDisplayName(field.getDisplayName() + " " + count);
		}
	}
	
	private Map<FieldType, List<String>> getColumnNamesGroupedByType(List<FacilioField> fields) {
		Map<FieldType, List<String>> existingColumns = new HashMap<>();
		if(fields != null) {
			for(FacilioField field : fields) {
				List<String> columns = existingColumns.get(field.getDataTypeEnum());
				if(columns == null) {
					columns = new ArrayList<>();
					existingColumns.put(field.getDataTypeEnum(), columns);
				}
				columns.add(field.getColumnName());
			}
		}
		return existingColumns;
	}
	
	private String getColumnNameForNewField(FieldType type, List<String> existingColumns) throws Exception {
		String[] columns = type.getColumnNames();
		if(columns != null && columns.length > 0) {
			if(existingColumns == null || existingColumns.size() == 0) {
				return columns[0];
			}
			else {
				for(String column : columns) {
					if(!existingColumns.contains(column)) {
						return column;
					}
				}
			}
		}
		return null;
	}
}