package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class FormulaAPI {
	
	public static Object getFormulaValue(Long formulaId) throws Exception {
		
		FormulaContext formulaContext = getFomulaContext(formulaId);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(formulaContext.getSelectField());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(formulaContext.getModule().getTableName())
				.andCriteria(formulaContext.getCriteria());
		
		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println("props 332 - "+props);
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
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
