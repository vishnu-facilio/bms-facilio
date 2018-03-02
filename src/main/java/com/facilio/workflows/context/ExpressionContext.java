package com.facilio.workflows.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser.ExpressionAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExpressionContext {
	
	String name;
	String moduleName;
	String fieldName;
	String aggregateString;
	Criteria criteria;
	FacilioModule module;
	FacilioField facilioField;
	Object value;
	Object exprResult;
	
	private static final String RESULT_STRING = "result";
	
	public Object getExprResult() {
		return exprResult;
	}

	public void setExprResult(Object result) {
		this.exprResult = result;
	}

	public boolean getIsOnlyValueExpression() {
		if(value != null) {
			return true;
		}
		return false;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getAggregateString() {
		return aggregateString;
	}
	public ExpressionAggregateOperator getAggregateOpperator() {
		return ExpressionAggregateOperator.getExpressionAggregateOperator(getAggregateString());
	}
	public FacilioField getFacilioField() throws Exception {
		if(this.facilioField == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			this.facilioField = modBean.getField(fieldName, moduleName);
		}
		return facilioField;
	}
	public void setFacilioField(FacilioField facilioField) {
		this.facilioField = facilioField;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}
	public FacilioModule getModule() throws Exception {
		if(this.module == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			this.module = modBean.getModule(getModuleName());
		}
		return this.module;
	}
	public void setAggregateString(String aggregateString) {
		this.aggregateString = aggregateString;
	}
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public Object getResult() throws Exception {
		
		if(getValue() != null) {
			return getValue();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(modBean.getModule(moduleName).getTableName())
				.andCriteria(criteria);
		
		if(modBean.getModule(moduleName).getExtendModule() != null) {
			selectBuilder.innerJoin(modBean.getModule(moduleName).getExtendModule().getTableName())
			.on(modBean.getModule(moduleName).getTableName()+".ID = "+modBean.getModule(moduleName).getExtendModule().getTableName()+".ID");
		}
		
		if(fieldName != null) {
			List<FacilioField> selectFields = new ArrayList<>();
			
			FacilioField select = getFacilioField();
			select.setColumnName(select.getExtendedModule().getTableName()+"."+select.getColumnName());
			select.setExtendedModule(null);
			select.setModule(null);
			select.setName(RESULT_STRING);
			
			if(aggregateString != null) {
				ExpressionAggregateOperator expAggregateOpp = getAggregateOpperator();
				select = expAggregateOpp.getSelectField(select);
				if(expAggregateOpp.equals(ExpressionAggregateOperator.FIRST_VALUE)) {
					selectBuilder.limit(1);
				}
			}
			selectFields.add(select);
			selectBuilder.select(selectFields);
		}
		else {
			selectBuilder.select(modBean.getAllFields(moduleName));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println("selectBuilder -- "+selectBuilder);
		System.out.println("selectBuilder result -- "+props);
		if(props != null && !props.isEmpty()) {
			if(getFieldName() == null && getAggregateString() == null) {
				exprResult = props;
			}
			if(getAggregateString() == null) {
				List<Object> returnList = new ArrayList<>(); 
				for(Map<String, Object> prop:props) {
					returnList.add(prop.get(RESULT_STRING));
				}
				exprResult = returnList;
			}
			else {
				exprResult = props.get(0).get(RESULT_STRING);
			}
		}
		System.out.println("EXP -- "+toString()+" RESULT -- "+exprResult);
		return exprResult;
	}
	
	@Override
	public String toString() {
		
		String result = "";
		result = result +"name -- "+getName() +"\n";
		result = result +"module -- "+getModuleName() +"\n";
		result = result +"field -- "+getFieldName() +"\n";
		result = result +"aggregate -- "+getAggregateString() +"\n";
		result = result +"value -- "+getValue() +"\n";
		return result;
	}
}
