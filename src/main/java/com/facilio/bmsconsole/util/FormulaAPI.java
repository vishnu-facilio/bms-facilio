package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormulaAPI {
	
	public static Object getFormulaValue(Long formulaId) throws Exception {
		
		FormulaContext formulaContext = getFomulaContext(formulaId);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(formulaContext.getSelectField());
		
		FacilioField specialType = new FacilioField();
		specialType.setName("parentId");
		specialType.setDataType(FieldType.NUMBER);
		specialType.setColumnName("PARENT_METER_ID");
		specialType.setModule(formulaContext.getModule());
		
		fields.add(specialType);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(formulaContext.getModule().getTableName())
				.andCriteria(formulaContext.getCriteria());
		
		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println("props --- "+props);
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static FormulaContext addFormula(FormulaContext formula) throws Exception {
		
		formula.setCriteriaId(CriteriaAPI.addCriteria(formula.getCriteria(), AccountUtil.getCurrentOrg().getOrgId()));
			List<FacilioField> fields = FieldFactory.getFormulaFields();
			formula.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getFormulaModule().getTableName())
					.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(formula);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			formula.setId((Long) props.get("id"));
		
		return formula;
	}
	
	public static FormulaContext getFomulaContext(Long formulaId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormulaFields())
				.table(ModuleFactory.getFormulaModule().getTableName())
				.andCustomWhere(ModuleFactory.getFormulaModule().getTableName()+".ID = ?", formulaId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			FormulaContext formulaContext = FieldUtil.getAsBeanFromMap(props.get(0), FormulaContext.class);
			return formulaContext;
		}
		return null;
	}

}
