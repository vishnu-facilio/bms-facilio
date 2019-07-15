package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class KPIUtil {
	
	public static final String KPI_CATEGORY_CONTEXT = "kpiCategoryContext";
	public static final String KPI_CATEGORY_CONTEXTS = "kpiCategoryContexts";
	
	public static void addKPICategoryContext(KPICategoryContext kpiCategoryContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.fields(FieldFactory.getKPICategoryFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(kpiCategoryContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		kpiCategoryContext.setId((Long) props.get("id"));
	}
	
	public static void updateKPICategoryContext(KPICategoryContext kpiCategoryContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.fields(FieldFactory.getKPICategoryFields())
				.andCondition(CriteriaAPI.getIdCondition(kpiCategoryContext.getId(), ModuleFactory.getKPICategoryModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(kpiCategoryContext);
		updateBuilder.update(props);
		
	}
	
	public static void deleteKPICategoryContext(KPICategoryContext kpiCategoryContext) {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getKPICategoryModule().getTableName())
		.andCondition(CriteriaAPI.getCondition("ID", "id", ""+kpiCategoryContext.getId(), NumberOperators.EQUALS));
	}
	
	public static KPICategoryContext getKPICategoryContext(long id) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getKPICategoryFields())
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getKPICategoryModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			KPICategoryContext kpiCategoryContext = FieldUtil.getAsBeanFromMap(props.get(0), KPICategoryContext.class);
			return kpiCategoryContext;
		}
		return null;
	}
	
	public static List<KPICategoryContext> getAllKPIContext() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getKPICategoryFields())
				.table(ModuleFactory.getKPICategoryModule().getTableName());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<KPICategoryContext> kpiCategoryContext = FieldUtil.getAsBeanListFromMapList(props, KPICategoryContext.class);
			return kpiCategoryContext;
		}
		return null;
	}
}
