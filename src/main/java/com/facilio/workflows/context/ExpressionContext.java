package com.facilio.workflows.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser.ExpressionAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExpressionContext {
	
	String expressionString;
	String name;
	String moduleName;
	String fieldName;
	String aggregateString;
	Criteria criteria;
	FacilioModule module;
	FacilioField facilioField;
	Object constant;
	Object exprResult;
	
	private static final String RESULT_STRING = "result";
	
	public String getExpressionString() {
		return expressionString;
	}

	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
	}
	
	public Object getExprResult() {
		return exprResult;
	}

	public void setExprResult(Object result) {
		this.exprResult = result;
	}

	public boolean getIsOnlyValueExpression() {
		if(constant != null) {
			return true;
		}
		return false;
	}
	
	public Object getConstant() {
		return constant;
	}
	public void setConstant(String constant) {
		this.constant = constant;
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
//	public FacilioField getFacilioField() throws Exception {
//		if(this.facilioField == null) {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			this.facilioField = modBean.getField(fieldName, moduleName);
//		}
//		return facilioField;
//	}
	public void setFacilioField(FacilioField facilioField) {
		this.facilioField = facilioField;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}
//	public FacilioModule getModule() throws Exception {
//		if(this.module == null) {
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			this.module = modBean.getModule(getModuleName());
//		}
//		return this.module;
//	}
	public void setAggregateString(String aggregateString) {
		this.aggregateString = aggregateString;
	}
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public boolean getIsLastNValuesExpression() {
		Criteria criteria = this.getCriteria();
		if(criteria != null) {
			Map<Integer, Condition> conditions = criteria.getConditions();
			if(conditions != null) {
				
				for(Integer key :conditions.keySet()) {
					
					Condition condition = conditions.get(key);
					if(condition.getOperator().equals(CommonOperators.LAST_N_READINGS)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public String getLastNValuesInnerQuery() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(this.getModuleName());
		
		Condition parentIdCondition = null,lastNreadingsCondition=null;
		Criteria criteria = this.getCriteria();
		if(criteria != null) {
			Map<Integer, Condition> conditions = criteria.getConditions();
			if(conditions != null) {
				
				for(Integer key :conditions.keySet()) {
					
					Condition condition = conditions.get(key);
					if(condition.getOperator().equals(CommonOperators.LAST_N_READINGS)) {
						lastNreadingsCondition = condition;
					}
					else if(condition.getFieldName().contains("parentId")) {
						parentIdCondition = condition;
					}
					
				}
			}
		}
		
		String s = "select ID from "+module.getTableName()+" Where "+parentIdCondition.getComputedWhereClause()+" order by TTIME desc limit "+lastNreadingsCondition.getValue();
		System.out.println("getLastNValuesInnerQuery -- "+s);
		return s;
	}
	
	public List<Condition> getLastNValuesRemainingCondition() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Condition> conditionList = new ArrayList<>();
		Criteria criteria = this.getCriteria();
		if(criteria != null) {
			Map<Integer, Condition> conditions = criteria.getConditions();
			if(conditions != null) {
				
				for(Integer key :conditions.keySet()) {
						
					Condition condition = conditions.get(key);
					if(condition.getOperator().equals(CommonOperators.LAST_N_READINGS) || condition.getFieldName().contains("parentId")) {
					}
					else {
						conditionList.add(condition);
					}
				}
			}
		}
		return conditionList;
	}
	
	public Object executeExpression() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = null;
		if(getConstant() != null) {
			return getConstant();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName); 
		
		if(getIsLastNValuesExpression()) {
			
			String innerQuery = getLastNValuesInnerQuery();
			selectBuilder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.innerJoin("("+ innerQuery + ") b")
					.on(module.getTableName()+".ID=b.ID")
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
			
			for(Condition condition:getLastNValuesRemainingCondition()) {
				selectBuilder.andCondition(condition);
			}
		}
		else {
			selectBuilder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCriteria(criteria)
					;
			
			if(modBean.getModule(moduleName).getExtendModule() != null) {
				selectBuilder.innerJoin(modBean.getModule(moduleName).getExtendModule().getTableName())
				.on(modBean.getModule(moduleName).getTableName()+".ID = "+modBean.getModule(moduleName).getExtendModule().getTableName()+".ID");
			}
		}
		
		if(fieldName != null) {
			List<FacilioField> selectFields = new ArrayList<>();
			
			FacilioField select = modBean.getField(fieldName, moduleName);
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
				else if(expAggregateOpp.equals(ExpressionAggregateOperator.LAST_VALUE)) {
					String parentIdString = null;
					Map<Integer, Condition> conditions = criteria.getConditions();
					for(Integer key:conditions.keySet()) {
						Condition condition = conditions.get(key);
						if(condition.getFieldName().contains("parentId")) {
							parentIdString = condition.getValue();
						}
					}
					exprResult = ReadingsAPI.getLastReadingValue(AccountUtil.getCurrentOrg().getId(), Long.parseLong(parentIdString), select);
					return exprResult;
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
		result = result +"constant -- "+getConstant() +"\n";
		return result;
	}
}
