package com.facilio.workflows.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.scriptengine.context.WorkflowFunctionContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.util.QueryUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class ExpressionContext implements WorkflowExpression {
	
	/**
	 * 
	 */
	
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}  
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ExpressionContext.class.getName());
	
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
	String printStatement;
	Object exprResult;
	String orderByFieldName;
	String sortBy;
	String limit;
	String groupBy;
	Map<Integer,Long> conditionSeqVsBaselineId;
	String expr;
	
	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}
	
	
	public String getPrintStatement() {
		return printStatement;
	}

	public void setPrintStatement(String printStatement) {
		this.printStatement = printStatement;
	}

	boolean isCustomFunctionResultEvaluator;
	WorkflowFunctionContext defaultFunctionContext;
	
	public WorkflowFunctionContext getDefaultFunctionContext() {
		return defaultFunctionContext;
	}

	public void setDefaultFunctionContext(WorkflowFunctionContext defaultFunctionContext) {
		this.defaultFunctionContext = defaultFunctionContext;
	}

	public boolean isCustomFunctionResultEvaluator() {
		return isCustomFunctionResultEvaluator;
	}

	public void setIsCustomFunctionResultEvaluator(boolean isCustomFunctionResultEvaluator) {
		this.isCustomFunctionResultEvaluator = isCustomFunctionResultEvaluator;
	}
	
	
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
	
	public String getGroupBy() {
		return groupBy;
	}
	
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
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
	public AggregateOperator getAggregateOpperator() {
		return AggregateOperator.getAggregateOperator(getAggregateString());
	}
	public void setFacilioField(FacilioField facilioField) {
		this.facilioField = facilioField;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
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
	
	Map<String,Object> variableToExpresionMap;
	
	public Map<String, Object> getVariableToExpresionMap() {
		return variableToExpresionMap;
	}

	public void setVariableToExpresionMap(Map<String, Object> variableToExpresionMap) {
		this.variableToExpresionMap = variableToExpresionMap;
	}

	public Object execute(WorkflowContext workflowContext) throws Exception {
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = null;
		if(isCustomFunctionResultEvaluator) {
			return WorkflowUtil.evalSystemFunctions(workflowContext.getGlobalParameters(),defaultFunctionContext,variableToExpresionMap, workflowContext);
		}
		if(getConstant() != null) {
			return getConstant();
		}
		if(getExpr() != null) {
			return WorkflowUtil.evaluateExpression(getExpr(), variableToExpresionMap, workflowContext.isIgnoreNullParams());
		}
		if(getPrintStatement() != null) {
			return printStatement(getPrintStatement(),variableToExpresionMap);
		}
		if(workflowContext.getCachedData() != null && !workflowContext.getCachedData().isEmpty()) {
			
			String parentId = WorkflowUtil.getParentIdFromCriteria(criteria);
			
			List<Map<String, Object>> cachedDatas = workflowContext.getCachedData().get(WorkflowUtil.getCacheKey(moduleName, parentId));

			if (cachedDatas != null && !cachedDatas.isEmpty()) {
				List<Map<String, Object>> passedData = new ArrayList<>();
				for(Map<String, Object> cachedData :cachedDatas) {
					org.apache.commons.collections.Predicate Predicate = criteria.computePredicate(cachedData);
					if(Predicate.evaluate(cachedData)) {
						passedData.add(cachedData);
					}
				}
				
				if (!passedData.isEmpty()) {
					if(getAggregateOpperator() != null) {
						exprResult =  getAggregateOpperator().getAggregateResult(passedData, fieldName);
					}
					if(exprResult != null) {
						return exprResult;
					}
				}
			}
		}
		List<Map<String, Object>> props = null;
		if (!LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(module.getTableName())
					.module(module)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
					.andCriteria(criteria)
					;
			
			if(workflowContext.getCriteria() != null) {
				selectBuilder.andCriteria(workflowContext.getCriteria());
			}
			
			if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() > 0) {
				selectBuilder.andCondition(CriteriaAPI.getCurrentSiteIdCondition(module));
			}
			if (AccountUtil.getCurrentUser() == null) {
				User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
				AccountUtil.getCurrentAccount().setUser(user);
			}
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
			if (scopeCriteria != null) {
				selectBuilder.andCriteria(scopeCriteria);
			}
			
			String parentIdString = null;
			Map<String, Condition> conditions = criteria.getConditions();
			for(String key:conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().contains("parentId")) {
					parentIdString = condition.getValue();
					break;
				}
			}
			
			if(parentIdString != null && FacilioUtil.isNumeric(parentIdString) && module.getTypeEnum() == ModuleType.READING) {
				FacilioModule parentModule = ReadingsAPI.getParentModuleRelFromChildModule(module);
				if(parentModule != null) {
					List<Map<String, Object>> rec = QueryUtil.fetchRecord(parentModule.getName(), Long.parseLong(parentIdString));
					if(rec == null || rec.isEmpty()) {
						return null;
					}
				}
			}
			
			
			if(fieldName != null && !isManualAggregateQuery()) {
				List<FacilioField> selectFields = new ArrayList<>();
				
				FacilioField selectOriginal = modBean.getField(fieldName, moduleName);
				
				FacilioField select = selectOriginal.clone();
				
				if(select == null) {
					throw new Exception("Field is null for FieldName - "+fieldName +" moduleName - "+moduleName);
				}
				
				select.setName(RESULT_STRING);
				
				selectBuilder.andCustomWhere(select.getCompleteColumnName()+" is not null");
				
				if(aggregateString != null && !aggregateString.isEmpty()) {
					AggregateOperator expAggregateOpp = getAggregateOpperator();
					selectBuilder.aggregate(expAggregateOpp, select);
					if(expAggregateOpp.equals(BmsAggregateOperators.SpecialAggregateOperator.FIRST_VALUE)) {
						selectBuilder.limit(1);
					}
					else if(expAggregateOpp.equals(BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE)) {
						boolean isLastValueWithTimeRange = false;
						
						conditions = criteria.getConditions();
						for(String key:conditions.keySet()) {
							Condition condition = conditions.get(key);
							if(condition.getFieldName().contains("ttime")) {
								isLastValueWithTimeRange = true;
								break;
							}
						}
						
						if(isLastValueWithTimeRange) {
							
							selectBuilder.limit(1);
							selectBuilder.orderBy("TTIME desc");
							
						}
						else {
							
							ReadingDataMeta readingDataMeta = null;
							if(workflowContext.getCachedRDM() != null && !workflowContext.getCachedRDM().isEmpty()) {
								String key = ReadingsAPI.getRDMKey(Long.parseLong(parentIdString), modBean.getField(fieldName, moduleName));
								readingDataMeta = workflowContext.getCachedRDM().get(key);
							}
							boolean isRDMFromCache = true;
							if(readingDataMeta == null) {
								isRDMFromCache = false;
								readingDataMeta = ReadingsAPI.getReadingDataMeta(Long.parseLong(parentIdString), select);						
								if(readingDataMeta!= null && readingDataMeta.getValue() != null && !readingDataMeta.getValue().equals("-1.0") && readingDataMeta.getField() != null && readingDataMeta.getField() instanceof NumberField) {
									NumberField numberField = (NumberField) readingDataMeta.getField();
									Object value = UnitsUtil.convertToDisplayUnit(readingDataMeta.getValue(), numberField);	
									if(value != null) {
										readingDataMeta.setValue(value);
										readingDataMeta.setActualValue(String.valueOf(value));
									}													
								}								
							}
							if(readingDataMeta == null) {
								throw new Exception("readingDataMeta is null for FieldName - "+fieldName +" moduleName - "+moduleName+" parentId - "+parentIdString);
							}
							long actualLastRecordedTime = CommonAPI.getActualLastRecordedTime(module);
							if(!isRDMFromCache && actualLastRecordedTime > 0) {
								if(readingDataMeta.getTtime() >= actualLastRecordedTime) {
									if(readingDataMeta.getActualValue() != null) {
										exprResult = readingDataMeta.getActualValue();
									}
									else {
										exprResult = readingDataMeta.getValue();
									}
								}
							}
							else {
								if(readingDataMeta.getActualValue() != null) {
									exprResult = readingDataMeta.getActualValue();
								}
								else {
									exprResult = readingDataMeta.getValue();
								}
							}
							if(exprResult != null && exprResult.toString().equals("-1")) {
								return null;
							}
							return exprResult;
						}
					}
					
					if(moduleName != null && moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING) && !workflowContext.isFetchMarkedReadings()) {
						FacilioField markedField = modBean.getField("marked", moduleName);
						if(markedField != null) {
							selectBuilder.andCondition(CriteriaAPI.getCondition(markedField, Boolean.FALSE.toString(), BooleanOperators.IS));
						}
					}
					if(workflowContext.isIgnoreMarkedReadings() && moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						
						FacilioField selectMarkedOriginal = modBean.getField("marked", moduleName);
						
						FacilioField selectMarked = selectMarkedOriginal.clone();
						if(selectMarked == null) {
							selectMarked = new FacilioField();
							selectMarked.setColumnName("MAX(MARKED)");
						}
						else {
							selectMarked.setColumnName("MAX("+selectMarked.getColumnName()+")");
						}
//						selectMarked.setExtendedModule(null);
						selectMarked.setModule(null);
						selectMarked.setName("hasMarked");
						
						selectFields.add(selectMarked);
					}
				}
				else {
					selectFields.add(select);	// check
				}
				
				selectBuilder.select(selectFields);
			}
			else {
				List<FacilioField> fields = new ArrayList<>(modBean.getAllFields(moduleName));
				fields.add(FieldFactory.getIdField(module));
				selectBuilder.select(fields);
			}
			
			if(getOrderByFieldName() != null) {
				FacilioField orderByField = modBean.getField(getOrderByFieldName(), moduleName);
				String orderByString = orderByField != null ? orderByField.getColumnName() : getOrderByFieldName();
				if(getSortBy() != null) {
					orderByString = orderByString +" "+getSortBy();
				}
				selectBuilder.orderBy(orderByString);
			}
			
			if(getLimit() != null) {
				selectBuilder.limit(Integer.parseInt(getLimit()));
			}
			if(getGroupBy() != null) {
				FacilioField groupByField = modBean.getField(getGroupBy(), moduleName);
				selectBuilder.groupBy(groupByField.getColumnName());
			}
			
			if(module != null && module.getName().equals("weather")) {						// temp handling must be removed (predicted weather data will be stored in same table with same module)
				selectBuilder.andCustomWhere("TTIME <= ?", DateTimeUtil.getCurrenTime());
			}
			if(AccountUtil.getCurrentOrg().getId() == 321l) {
				
				Long endTime = WorkflowUtil.demoCheckGetEndTime(module, criteria);
				if(endTime > 0) {
					selectBuilder.andCustomWhere("TTIME <= ?",endTime);
				}
			}
			props = selectBuilder.getAsProps();
			
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
		LOGGER.fine("selectBuilder -- "+selectBuilder);
		LOGGER.fine("selectBuilder result -- "+props);
		
		if(props != null && !props.isEmpty()) {
			
			if(isManualAggregateQuery()) {
				List<Map<String, Object>> passedData = new ArrayList<>();
				Long ttimeCount = 0l;
				for(int i = 0;i<props.size();i++) {
					
					Map<String, Object> prop = props.get(i);
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
			else if(getAggregateString() == null || getAggregateString().equals("")) {
				List<Object> returnList = new ArrayList<>(); 
				for(Map<String, Object> prop:props) {
					returnList.add(prop.get(RESULT_STRING));
				}
				exprResult = returnList;
			}
			else {
				// Temp check
				String name = LookupSpecialTypeUtil.isSpecialType(moduleName) ? fieldName : RESULT_STRING;
				exprResult = props.get(0).get(name);
				
				if(workflowContext.isIgnoreMarkedReadings() && moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
					
					Object hasMarked = props.get(0).get("hasMarked");
					
					if(hasMarked != null && "1".equals(hasMarked.toString())) {
						workflowContext.setTerminateExecution(true);
					}
 				}
			}
		}
		LOGGER.fine("EXP -- "+toString()+" RESULT -- "+exprResult);
		return exprResult;
	}
	
	private boolean isManualAggregateQuery() {
		if((getAggregateCondition() != null && !getAggregateCondition().isEmpty())) {
			return true;
		}
		return false;
	}
	
	private Object printStatement(String printStatement,Map<String,Object> variableToExpresionMap1) {
		String[] splitedPrints = printStatement.split("\\+");
		
		String finalResult = "";
		
		for(int i=0 ;i<splitedPrints.length;i++) {
			splitedPrints[i] = splitedPrints[i].trim();
			if(splitedPrints[i].startsWith("'") && splitedPrints[i].endsWith("'")) {
				splitedPrints[i] = splitedPrints[i].substring(1, splitedPrints[i].length()-1);
			}
			else {
				if(variableToExpresionMap1.containsKey(splitedPrints[i]) && variableToExpresionMap1.get(splitedPrints[i]) != null) {
					splitedPrints[i] = variableToExpresionMap1.get(splitedPrints[i]).toString();
				}
				else {
					splitedPrints[i] = "null";
				}
			}
			finalResult = finalResult + splitedPrints[i];
		}
		LOGGER.log(Level.SEVERE, finalResult);
		return null;
	}
	
	@Override
	public String toString() {
		
		String result = "";
		result = result +"name -- "+getName() +"\n";
		result = result +"module -- "+getModuleName() +"\n";
		result = result +"field -- "+getFieldName() +"\n";
		result = result +"aggregate -- "+getAggregateString() +"\n";
		result = result +"constant -- "+getConstant() +"\n";
		result = result +"criteria -- "+getCriteria() +"\n";
		return result;
	}

	@Override
	public int getWorkflowExpressionType() {
		// TODO Auto-generated method stub
		return WorkflowExpressionType.EXPRESSION.getValue();
	}
}
