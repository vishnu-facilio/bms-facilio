package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.context.BaseLineContext.RangeType;
import com.facilio.db.criteria.CommonOperators;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class BaseLineAPI {
	
	public static long addBaseLine(BaseLineContext baseLine) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, RuntimeException {
		baseLine.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> blProps = FieldUtil.getAsProperties(baseLine);
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
															.fields(FieldFactory.getBaseLineFields())
															.table(ModuleFactory.getBaseLineModule().getTableName())
															.addRecord(blProps);
															;
		insertRecordBuilder.save();
		
		if(baseLine.getReportId() != -1) {
			addReportBaseLines(baseLine.getReportId(), Collections.singletonList(baseLine));
		}
		
		return (long) blProps.get("id");
	}
	
	public static void addReportBaseLines(long reportId, List<BaseLineContext> baseLines) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder relBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getBaseLineReportRelModule().getTableName())
													.fields(FieldFactory.getBaseLineReportsRelFields())
													;
		
		for (BaseLineContext baseLine : baseLines) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("id", baseLine.getId());
			prop.put("reportId", reportId);
			prop.put("adjustType", baseLine.getAdjustType());
			
			relBuilder.addRecord(prop);
		}
		
		relBuilder.save();
	}
	
	public static int deleteExistingReportBaseLines(long reportId) throws SQLException {
		FacilioField reportField = FieldFactory.getAsMap(FieldFactory.getBaseLineReportsRelFields()).get("reportId");
		FacilioModule module = ModuleFactory.getBaseLineReportRelModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(reportField, String.valueOf(reportId), NumberOperators.EQUALS));
		
		return deleteBuilder.delete();
	}
	
	public static List<BaseLineContext> getAllBaseLines() throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		
		return getBaseLinesFromProps(selectBuilder.get());
	}
	
	public static List<BaseLineContext> getBaseLinesOfSpace(long spaceId) throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		FacilioField spaceField = FieldFactory.getAsMap(fields).get("spaceId");
		
		Criteria spaceCriteria = new Criteria();
		spaceCriteria.addOrCondition(CriteriaAPI.getCondition(spaceField, String.valueOf(spaceId), NumberOperators.EQUALS));
		spaceCriteria.addOrCondition(CriteriaAPI.getCondition(spaceField, String.valueOf(spaceId), CommonOperators.IS_EMPTY));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCriteria(spaceCriteria)
														;
		
		return getBaseLinesFromProps(selectBuilder.get());
	}
	
	public static BaseLineContext getBaseLine(long id) throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		List<BaseLineContext> baseLines = getBaseLinesFromProps(selectBuilder.get());
		if (baseLines != null && !baseLines.isEmpty()) {
			return baseLines.get(0);
		}
		return null;
	}
	
	public static BaseLineContext getBaseLine(RangeType rangeType) throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("RANGE_TYPE", "RANGE_TYPE", rangeType.getVal()+"", NumberOperators.EQUALS))
														;
		
		List<BaseLineContext> baseLines = getBaseLinesFromProps(selectBuilder.get());
		if (baseLines != null && !baseLines.isEmpty()) {
			return baseLines.get(0);
		}
		return null;
	}
	
	public static List<BaseLineContext> getBaseLinesOfReport(long reportId) throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		FacilioModule relModule = ModuleFactory.getBaseLineReportRelModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		fields.addAll(FieldFactory.getBaseLineReportsRelFields());
		FacilioField reportField = FieldFactory.getAsMap(fields).get("reportId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.innerJoin(relModule.getTableName())
														.on(module.getTableName()+".ID = "+relModule.getTableName()+".BASE_LINE_ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(reportField, String.valueOf(reportId), NumberOperators.EQUALS))
														;
		
		return getBaseLinesFromProps(selectBuilder.get());
	}
	
	private static List<BaseLineContext> getBaseLinesFromProps(List<Map<String, Object>> props) {
		if(props != null && !props.isEmpty()) {
			List<BaseLineContext> baseLines = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				baseLines.add(FieldUtil.getAsBeanFromMap(prop, BaseLineContext.class));
			}
			return baseLines;
		}
		return null;
	}
	
	public static Map<Long, BaseLineContext> getBaseLinesAsMap (List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getBaseLineModule();
		List<FacilioField> fields = FieldFactory.getBaseLineFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		
		return getBaseLinesAsMapFromProps(selectBuilder.get());
	}
	
	private static Map<Long, BaseLineContext> getBaseLinesAsMapFromProps(List<Map<String, Object>> props) {
		if(props != null && !props.isEmpty()) {
			Map<Long, BaseLineContext> baseLines = new HashMap<>();
			for (Map<String, Object> prop : props) {
				BaseLineContext baseLine = FieldUtil.getAsBeanFromMap(prop, BaseLineContext.class); 
				baseLines.put(baseLine.getId(), baseLine);
			}
			return baseLines;
		}
		return null;
	}
}
