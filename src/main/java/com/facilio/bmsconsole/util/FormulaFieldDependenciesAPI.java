package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FormulaFieldDependenciesAPI {
	
	public static void addFormulaFieldDependencies(List<FormulaFieldDependenciesContext> formulaFieldDependencyContextList) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.fields(FieldFactory.getFormulaFieldDependenciesFields());
		
		for(FormulaFieldDependenciesContext formulaFieldDependencyContext : formulaFieldDependencyContextList)
		{
			Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldDependencyContext);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();

	}
	
	public static void addFormulaFieldDependencies(FormulaFieldDependenciesContext formulaFieldDependencyContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.fields(FieldFactory.getFormulaFieldDependenciesFields());
	
		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldDependencyContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		formulaFieldDependencyContext.setId((Long) props.get("id"));			
	}
	
	
	public static FormulaFieldDependenciesContext getFormulaFieldDependencyById (long id) throws Exception {
					
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldDependenciesFields())
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			FormulaFieldDependenciesContext formulaFieldDependencyContext = FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldDependenciesContext.class);
			return formulaFieldDependencyContext;
		}
		return null;
	}
	
	public static List<FormulaFieldDependenciesContext> getFormulaFieldDependencyByParentFormula(long parentFormulaResourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldDependenciesFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldDependenciesFields())
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentFormulaResourceId"), ""+ parentFormulaResourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldDependenciesContext> formulaFieldDependenciesContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldDependenciesContext.class);
			return formulaFieldDependenciesContextList;
		}
		return null;
	}
	
	public static List<FormulaFieldDependenciesContext> getFormulaFieldDependencyByDependentFormula(long dependentFormulaResourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldDependenciesFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldDependenciesFields())
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("dependentFormulaResourceId"), ""+ dependentFormulaResourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldDependenciesContext> formulaFieldDependenciesContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldDependenciesContext.class);
			return formulaFieldDependenciesContextList;
		}
		return null;
	}
	
	public static FormulaFieldDependenciesContext getFormulaFieldDependenciesByFieldAndResource(long fieldId, long resourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldDependenciesFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldDependenciesFields())
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), ""+ fieldId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+ resourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			FormulaFieldDependenciesContext formulaFieldDependenciesContext =  FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldDependenciesContext.class);
			return formulaFieldDependenciesContext;
		}
		return null;
	}
	
	public static void updateFormulaFieldDependencies(FormulaFieldDependenciesContext formulaFieldDependencyContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
				.fields(FieldFactory.getFormulaFieldDependenciesFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+formulaFieldDependencyContext.getId(), NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldDependencyContext);
		updateBuilder.update(props);
	}
	
	
	public static void deleteFormulaFieldDependencies(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder
			.table(ModuleFactory.getFormulaFieldDependenciesModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
	}
	
	public static List<FormulaFieldContext> getFormulaFieldContextByReadingFieldIds(List<Long> readingFieldIds) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldFields());	
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldFields())
				.table(ModuleFactory.getFormulaFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingFieldId"), ""+ readingFieldIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldContext> formulaFieldContextList =  FieldUtil.getAsBeanListFromMapList(props, FormulaFieldContext.class);
			return formulaFieldContextList;
		}
		return null;	
	}
	
	public static void setFormulaFieldDependenciesContext(long dependentFieldId, long dependentResourceId, long parentResourceId, List<FormulaFieldDependenciesContext> formulaFieldDependenciesContextList) throws Exception {
		
		FormulaFieldDependenciesContext formulaFieldDependencyContext = new FormulaFieldDependenciesContext();
		
		formulaFieldDependencyContext.setFieldId(dependentFieldId);
		formulaFieldDependencyContext.setResourceId(dependentResourceId);       		//taking dependency field resource as parent associated resource for granted as the dependent field resource is null	
		formulaFieldDependencyContext.setParentFormulaResourceId(parentResourceId);
																				
		Long formulaFieldResourceStatusId = FormulaFieldResourceStatusAPI.getFormulaFieldResourceIdStatusByFieldAndResource(dependentFieldId, dependentResourceId); 	//dependent resource_formulaId from formula
		formulaFieldDependencyContext.setDependentFormulaResourceId(formulaFieldResourceStatusId);
		
		formulaFieldDependenciesContextList.add(formulaFieldDependencyContext);
	}

}
