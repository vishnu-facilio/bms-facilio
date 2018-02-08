package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ExpressionContext;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExpressionAPI {
	
	public static Long addExpression(ExpressionContext expressionContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getExpressionModule().getTableName())
				.fields(FieldFactory.getExpressionFields());

		Map<String, Object> props = FieldUtil.getAsProperties(expressionContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		expressionContext.setId((Long) props.get("id"));
		return (Long) props.get("id");
	}
	
	public static ExpressionContext getExpressionContext(Long expressionId) throws Exception  {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getExpressionFields())
				.table(ModuleFactory.getExpressionModule().getTableName())
				.andCustomWhere(ModuleFactory.getExpressionModule().getTableName()+".ID = ?", expressionId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		ExpressionContext expressionContext = null;
		if (props != null && !props.isEmpty()) {
			expressionContext = FieldUtil.getAsBeanFromMap(props.get(0), ExpressionContext.class);
		}
		return expressionContext;
	}
	
	public static Object getResult(String expressionString) throws Exception {

		FacilioExpressionParser fc = new FacilioExpressionParser(expressionString);
		Object result = fc.getResult();
		return result;
	}
	public static Object getResult(Long expressionId)  throws Exception  {
	
		ExpressionContext expressionContext = getExpressionContext(expressionId);
		FacilioExpressionParser fc = new FacilioExpressionParser(expressionContext.getExpressionString());
		Object result = fc.getResult();
		return result;
	}
	public static Object getResult(ExpressionContext expressionContext)  throws Exception  {
		
		if(expressionContext.getExpressionString() != null) {
			FacilioExpressionParser fc = new FacilioExpressionParser(expressionContext.getExpressionString());
			Object result = fc.getResult();
			return result;
		}
		return null;
	}
}
