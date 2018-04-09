package com.facilio.workflows.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.FacilioModulePredicate;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.util.ExpressionAggregateOperator;

public class ExpressionContext {
	
	String expressionString;
	String name;
	String moduleName;
	String fieldName;
	String aggregateString;
	List<Condition> aggregateCondition;
	Criteria criteria;
	FacilioModule module;
	FacilioField facilioField;
	Object constant;
	Object exprResult;
	String orderByFieldName;
	String sortBy;
	String limit;
	Map<Integer,Long> conditionSeqVsBaselineId;
	
	public Map<Integer, Long> getConditionSeqVsBaselineId() {
		return conditionSeqVsBaselineId;
	}
	public void addConditionSeqVsBaselineId(Integer conditionSeq,Long baselineId) {
		if(conditionSeqVsBaselineId == null) {
			conditionSeqVsBaselineId = new HashMap<Integer, Long>();
		}
		conditionSeqVsBaselineId.put(conditionSeq, baselineId);
	}
	public void setConditionSeqVsBaselineId(Map<Integer, Long> conditionSeqVsBaselineId) {
		this.conditionSeqVsBaselineId = conditionSeqVsBaselineId;
	}

	private static final String RESULT_STRING = "result";
	
	public String getOrderByFieldName() {
		return orderByFieldName;
	}

	public void setOrderByFieldName(String orderByFieldName) {
		this.orderByFieldName = orderByFieldName;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public List<Condition> getAggregateCondition() {
		return aggregateCondition;
	}

	public void setAggregateCondition(List<Condition> aggregateCondition) {
		this.aggregateCondition = aggregateCondition;
	}
	
	public void addAggregateCondition(Condition aggregateCondition) {
		if(this.aggregateCondition == null) {
			this.aggregateCondition = new ArrayList<>();
		}
		this.aggregateCondition.add(aggregateCondition);
	}
	
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
	
	public Object executeExpression() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = null;
		if(getConstant() != null) {
			return getConstant();
		}
		List<Map<String, Object>> props = null;
		if (!LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName); 
			
			selectBuilder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
					.andCriteria(criteria)
					;
			
			if(modBean.getModule(moduleName).getExtendModule() != null) {
				selectBuilder.innerJoin(modBean.getModule(moduleName).getExtendModule().getTableName())
				.on(modBean.getModule(moduleName).getTableName()+".ID = "+modBean.getModule(moduleName).getExtendModule().getTableName()+".ID");
			}
			
			if(fieldName != null && !isManualAggregateQuery()) {
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
						exprResult = ReadingsAPI.getLastReadingValue(Long.parseLong(parentIdString), select);
						return exprResult;
					}
				}
				selectFields.add(select);
				selectBuilder.select(selectFields);
			}
			else {
				selectBuilder.select(modBean.getAllFields(moduleName));
			}
			
			if(getOrderByFieldName() != null) {
				FacilioField orderByField = modBean.getField(getOrderByFieldName(), moduleName);
				String orderByString = orderByField.getColumnName();
				if(getSortBy() != null) {
					orderByString = orderByString +" "+getSortBy();
				}
				selectBuilder.orderBy(orderByString);
			}
			if(getLimit() != null) {
				selectBuilder.limit(Integer.parseInt(getLimit()));
			}
			
			props = selectBuilder.get();
		}
		else {
			List records = LookupSpecialTypeUtil.getObjects(moduleName, criteria);
			if (records != null) {
				props = new ArrayList<>();
				for (Object record : records) {
					props.add(FieldUtil.getAsProperties(record));
				}
			}
		}
		System.out.println("selectBuilder -- "+selectBuilder);
		System.out.println("selectBuilder result -- "+props);
		if(props != null && !props.isEmpty()) {
			
			if(isManualAggregateQuery()) {
				List<Map<String, Object>> passedData = new ArrayList<>();
				
				for(Map<String, Object> prop:props) {
					boolean isPassedData = true;
					if(aggregateCondition != null) {
						for(Condition condition:aggregateCondition) {
							FacilioModulePredicate predicate = condition.getOperator().getPredicate(condition.getFieldName(), condition.getValue());
							boolean result = predicate.evaluate(prop);
							isPassedData = isPassedData && result;
						}
					}
					if(isPassedData) {
						passedData.add(prop);
					}
				}
				if(getAggregateOpperator() != null) {
					return getAggregateOpperator().getAggregateResult(passedData, fieldName);
				}
				else {
					return passedData;
				}
			}
			
			if(getFieldName() == null && getAggregateString() == null) {
				if(props.size() == 1) {
					exprResult = props.get(0);
				}
				else {
					exprResult = props;
				}
			}
			else if(getAggregateString() == null) {
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
	
	private boolean isManualAggregateQuery() {
		if((getAggregateCondition() != null && !getAggregateCondition().isEmpty())) {
			return true;
		}
		return false;
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
