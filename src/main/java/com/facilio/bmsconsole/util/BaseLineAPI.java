package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

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
			prop.put("isAdjust", baseLine.isAdjust());
			
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
		
		return getBaseLinesFromMap(selectBuilder.get());
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
		
		return getBaseLinesFromMap(selectBuilder.get());
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
		
		return getBaseLinesFromMap(selectBuilder.get());
	}
	
	private static List<BaseLineContext> getBaseLinesFromMap(List<Map<String, Object>> props) {
		if(props != null && !props.isEmpty()) {
			List<BaseLineContext> baseLines = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				baseLines.add(FieldUtil.getAsBeanFromMap(prop, BaseLineContext.class));
			}
			return baseLines;
		}
		return null;
	}
}
