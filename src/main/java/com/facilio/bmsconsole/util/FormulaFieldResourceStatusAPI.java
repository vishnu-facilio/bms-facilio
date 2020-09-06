package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.context.FormulaFieldDependenciesContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FormulaFieldResourceStatusAPI {
	
public static void addFormulaFieldResourceStatus(List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		for(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext : formulaFieldResourceStatusContextList)
		{
			Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldResourceStatusContext);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();		

	}
	
	public static void addFormulaFieldResourceStatus(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields());
	
		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldResourceStatusContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		formulaFieldResourceStatusContext.setId((Long) props.get("id"));			
	}
	
	
	public static FormulaFieldResourceStatusContext getFormulaFieldResourceStatusById (long id) throws Exception {
					
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			FormulaFieldResourceStatusContext formulaFieldResourceStatusContext = FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContext;
		}
		return null;
	}
	
	public static List<FormulaFieldResourceStatusContext> getFormulaFieldResourceStatusByIds(List<Long> ids) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContextList;
		}
		return null;
	}
	
	public static List<FormulaFieldResourceStatusContext> getFormulaFieldResourceStatusByFrequencyAndIds(List<Long> ids, List<Integer> types) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), StringUtils.join(types, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContextList;
		}
		return null;
	}
	
	public static List<Long> getFormulaFieldResourceStatusIdsByFrequencyAndIds(List<Long> ids, List<Integer> types) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(fieldMap.get("id")))
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), StringUtils.join(types, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> formulaFieldResourceIds = new ArrayList<Long>();
		
		if (props != null && !props.isEmpty()) {	
			for(Map<String, Object> prop : props) {
				if(prop.get("id") != null) {
					formulaFieldResourceIds.add((long) prop.get("id"));
				}
			}	
		}
		return formulaFieldResourceIds;
	}
	
	public static List<FormulaFieldResourceStatusContext> getActiveFormulaFieldResourceStatusByIds(List<Long> ids) throws Exception {
		
		if(ids != null && !ids.isEmpty())
		{
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
					.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {			
				List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldResourceStatusContext.class);
				return formulaFieldResourceStatusContextList;
			}		
		}	
		
		return null;
	}
	
	public static long getCompletedFormulaFieldResourceStatusCountByIds(List<Long> ids) throws Exception {
		
		long count = 0;
		if(ids != null && !ids.isEmpty())
		{
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
			
			FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("id"));
			countField.setName("count");			
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(countField))
					.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty() && props.get(0) != null) {	
				Map<String, Object> prop = props.get(0);
				if(prop.get("count") != null)
				{
					count = (long) prop.get("count");
					return count;
				}
			}		
		}	
		return count;
	}

	public static List<FormulaFieldResourceStatusContext> getFormulaFieldResourceStatusByFormula(long formulaFieldId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldId"), ""+ formulaFieldId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContextList;
		}
		return null;
	}
	
	public static List<FormulaFieldResourceStatusContext> getLeafFormulaFieldResourceStatusByFrequency(List<Integer> types) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("frequency"), StringUtils.join(types, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("isLeaf"), ""+ true, BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = FieldUtil.getAsBeanListFromMapList(props, FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContextList;
		}
		return null;
	}
	
	public static FormulaFieldResourceStatusContext getFormulaFieldResourceStatusByFormulaAndResource(long formulaFieldId, long resourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldId"), ""+ formulaFieldId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+ resourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			FormulaFieldResourceStatusContext formulaFieldResourceStatusContext =  FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContext;
		}
		return null;
	}
	
	public static Long getFormulaFieldResourceIdStatusByFieldAndResource(long fieldId, long resourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), ""+ fieldId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), ""+ resourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			FormulaFieldResourceStatusContext formulaFieldResourceStatusContext =  FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldResourceStatusContext.class);
			return formulaFieldResourceStatusContext.getId();
		}
		return null;
	}
	
	public static void updateFormulaFieldResourceStatus(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+formulaFieldResourceStatusContext.getId(), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldResourceStatusContext);
		updateBuilder.update(props);
	}
	
	public static int updateInProgressFormulaFieldResourceStatus(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+formulaFieldResourceStatusContext.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldResourceStatusContext);
		int rowsUpdated = updateBuilder.update(props);
		return rowsUpdated;
	}
	
	public static int updateCompletedFormulaFieldResourceStatus(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext) throws Exception {
			
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+formulaFieldResourceStatusContext.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), ""+ FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(formulaFieldResourceStatusContext);
		int rowsUpdated = updateBuilder.update(props);
		return rowsUpdated;
	}
	
	public static void updateIsLeafField(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
				.fields(FieldFactory.getFormulaFieldResourceStatusModuleFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+formulaFieldResourceStatusContext.getId(), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("isLeaf", (Boolean)formulaFieldResourceStatusContext.getLeaf());
		updateBuilder.update(props);
	}
	
	
	public static void deleteFormulaFieldResourceStatus(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder
			.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
	}
	
	public static void deleteFormulaFieldResourceStatusByFormulaId(long formulaFieldId) {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldResourceStatusModuleFields());
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder
			.table(ModuleFactory.getFormulaFieldResourceStatusModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("formulaFieldId"), ""+ formulaFieldId, NumberOperators.EQUALS));
	}
	
	public static void rollUpIsLeafBasedOnDependencies(List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList, List<FormulaFieldDependenciesContext> formulaFieldDependenciesContextList) throws Exception
	{	
		Map<Long, FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextMap = new LinkedHashMap<Long, FormulaFieldResourceStatusContext>();
		Map<Long, List<FormulaFieldDependenciesContext>> formulaFieldResourceVsDependenciesContextMap = new LinkedHashMap<Long, List<FormulaFieldDependenciesContext>>(); //Keys are same for both these Maps
		
		for(FormulaFieldResourceStatusContext formulaFieldResourceStatusContext: formulaFieldResourceStatusContextList)
		{
			formulaFieldResourceStatusContext.setLeaf(true);   			//Setting all formulaResource isLeaf as false as dependencies are not formula till here
			formulaFieldResourceStatusContextMap.put(formulaFieldResourceStatusContext.getId(),formulaFieldResourceStatusContext);						
		}
		
		for(FormulaFieldDependenciesContext formulaFieldDependenciesContext: formulaFieldDependenciesContextList)
		{
			if(formulaFieldResourceVsDependenciesContextMap.containsKey(formulaFieldDependenciesContext.getParentFormulaResourceId()))
			{
				List<FormulaFieldDependenciesContext> formulaFieldDependenciesList = formulaFieldResourceVsDependenciesContextMap.get(formulaFieldDependenciesContext.getParentFormulaResourceId());
				formulaFieldDependenciesList.add(formulaFieldDependenciesContext);
			}
			else
			{
				List<FormulaFieldDependenciesContext> formulaFieldDependenciesList = new ArrayList<FormulaFieldDependenciesContext>();
				formulaFieldDependenciesList.add(formulaFieldDependenciesContext);
				formulaFieldResourceVsDependenciesContextMap.put(formulaFieldDependenciesContext.getParentFormulaResourceId(), formulaFieldDependenciesList);
			}
		}
		
		for(Long formulaFieldResourceStatusId: formulaFieldResourceStatusContextMap.keySet())
		{
			List<FormulaFieldDependenciesContext> formulaFieldDependenciesList = formulaFieldResourceVsDependenciesContextMap.get(formulaFieldResourceStatusId);
			for(FormulaFieldDependenciesContext formulaFieldDependency:formulaFieldDependenciesList)
			{
				if(formulaFieldDependency.getDependentFormulaResourceId() != null)
				{
					FormulaFieldResourceStatusContext formulaFieldResourceStatusContext = formulaFieldResourceStatusContextMap.get(formulaFieldResourceStatusId);
					formulaFieldResourceStatusContext.setLeaf(false);
				}
			}
			FormulaFieldResourceStatusAPI.updateIsLeafField(formulaFieldResourceStatusContextMap.get(formulaFieldResourceStatusId));					
		}
	}

}
