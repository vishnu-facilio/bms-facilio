package com.facilio.bmsconsole.criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.util.ExpressionAggregateOperator;
import com.udojava.evalex.Expression;

public class FacilioExpressionParser {

	private static final String EXPRESSION_FORMATTER = "((.+?)\\s*)(?:\\[+)(.+?)(?:\\]+)(?:\\.*)([^.]*)(?:\\.*)([^.]*)";
	
	private static final String RETURN_SINGLE_VALUE_EXPRESSION_FORMATTER = "((.+?)\\s*)(?:\\[+)(.+?)(?:\\]+)(?:\\.+)([^.]+)(?:\\.+)([^.]+)$";
	private static final String RETURN_LIST_VALUE_EXPRESSION_FORMATTER = "((.+?)\\s*)(?:\\[+)(.+?)(?:\\]+)(?:\\.+)([^.]+)$";
	private static final String RETURN_MAP_VALUE_EXPRESSION_FORMATTER = "((.+?)\\s*)(?:\\[+)(.+?)(?:\\]+)$";
	private static final String RETURN_BOOLEAN_VALUE_EXPRESSION_FORMATTER = ".+\\s*\\$\\$\\s*(<|<=|>=|>|=|!=)\\s*\\$\\$\\s*.+";
	private static final String COMPARATOR_FORMATOR = "\\s*\\$\\$\\s*(<|<=|>=|>|=|!=|==|<>)\\s*\\$\\$\\s*";
	private static final String CONDITION_FORMATTER = "((.*?)`(baseLine\\{(\\d+)\\}\\s*)?([^`]*)`(.*))";
	private static final String CONDITION_SPACE_SEPERATOR = "##";
	private static final String EXPRESSION_SPACE_SEPERATOR = "``";
	private static final String COMPARATOR_SPACE_SEPERATOR = "$$";
	private static final String RESULT_STRING = "result";
	private static final String NUMBER_CONSTANT_FORMATER = "[0-9]+";
	private static final String EMPTY_STRING = "";
	
	String expressionString;
	
	public String getExpressionString() {
		return expressionString;
	}
	
	public void setExpressionString(String expressionString) {
		this.expressionString = expressionString;
	}
	public FacilioExpressionParser(String expressionString) {
		this.expressionString = expressionString;
	}
	public Object getResult() throws Exception {
		
		Object result = null;
		
		//String expressionString = "``alarm [isAcknowledged`is`true (##) && (##) state`is`Alarm].state.count``+``alarm [isAcknowledged`is`true (##) && (##) state`is`Alarm].state.count``*``10``>``alarm [isAcknowledged`is`true (##) && (##) state`is`Alarm].state.count``*``200``"; //test data
		String[] values = expressionString.split(EXPRESSION_SPACE_SEPERATOR);
		if(values.length == 2 && values[0].equals(EMPTY_STRING)) {
			FacilioSubExpressionParser facilioSubExpressionParser = new FacilioSubExpressionParser();
			facilioSubExpressionParser.setExpressionString(values[1]);
			facilioSubExpressionParser.parseExpression();
			result = facilioSubExpressionParser.getResult();
			return result;
		}
		else {
			char variable = 'a';
			Map<Character,String> variableToExpresionMap = new HashMap<Character,String>();
			StringBuilder expString = new StringBuilder();
			for(String value:values) {
				if(value == null || value.trim().equals(EMPTY_STRING)) {
					continue;
				}
				value = value.trim();
				if(isComparitionOpperator(value)) {
					expString.append(getComparitionOpperator(value));
				}
				else if(isOperators(value)) {
					expString.append(value);
				}
				else {
					String subExpResult = null;
					if(isSingleValueReturnTypeExpression(value)) {
						FacilioSubExpressionParser facilioSubExpressionParser = new FacilioSubExpressionParser();
						facilioSubExpressionParser.setExpressionString(value);
						facilioSubExpressionParser.parseExpression();
						subExpResult = (String) facilioSubExpressionParser.getResult();
					}
					else if(isNumber(value)) {
						subExpResult = value;
					}
					else {
						throw new Exception("Cannot parse expression "+value);
					}
					expString.append(variable);
					
					variableToExpresionMap.put(variable++, subExpResult);
				}
			}
			System.out.println("expString --- "+expString);
			System.out.println("variableToExpresionMap --- "+variableToExpresionMap);
			result =  evaluateExpression(expString.toString(),variableToExpresionMap);
			System.out.println("result --- "+result);
			return result;
		}
	}
	
	public Object evaluateExpression(String exp,Map<Character,String> variablesMap) {
		Expression expression = new Expression(exp);
		for(Character key : variablesMap.keySet()) {
			String value = variablesMap.get(key);
			expression.with(""+key, value);
		}
		BigDecimal result = expression.eval();
		return result.doubleValue();
	}
	
	public static boolean isComparitionOpperator(String value) {
		Pattern pattern = Pattern.compile(COMPARATOR_FORMATOR);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}
	public static String getComparitionOpperator(String value) {
		Pattern pattern = Pattern.compile(COMPARATOR_FORMATOR);
		Matcher matcher = pattern.matcher(value);
		while(matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	public static boolean isOperators(String value) {
		if(value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/") || value.equals("%") || value.equals("^") || value.equals("=") || value.equals("==") || value.equals("!=") || value.equals("<") || value.equals("<=") || value.equals(">") || value.equals(">=") || value.equals("(") || value.equals(")")) {
			return true;
		}
		return false;
	}
	
	public static boolean isSingleValueReturnTypeExpression(String expressionString) {
		Pattern pattern = Pattern.compile(RETURN_SINGLE_VALUE_EXPRESSION_FORMATTER);
		Matcher matcher = pattern.matcher(expressionString);
		return matcher.find();
	}
	public static boolean isListValueReturnTypeExpression(String expressionString) {
		Pattern pattern = Pattern.compile(RETURN_LIST_VALUE_EXPRESSION_FORMATTER);
		Matcher matcher = pattern.matcher(expressionString);
		return matcher.find();
	}
	public static boolean isMapValueReturnTypeExpression(String expressionString) {
		Pattern pattern = Pattern.compile(RETURN_MAP_VALUE_EXPRESSION_FORMATTER);
		Matcher matcher = pattern.matcher(expressionString);
		return matcher.find();
	}
	public static boolean isNumber(String expression) {
		Pattern pattern = Pattern.compile(NUMBER_CONSTANT_FORMATER);
		Matcher matcher = pattern.matcher(expression);
		return matcher.find();
	}
	public static boolean isBooleanValueReturnTypeExpression(String expression) {
		Pattern pattern = Pattern.compile(RETURN_BOOLEAN_VALUE_EXPRESSION_FORMATTER);
		Matcher matcher = pattern.matcher(expression);
		return matcher.find();
	}
	
	
	private class FacilioSubExpressionParser {
		
		public void parseExpression() throws Exception {
			
			//templateString = "alarm [isAcknowledged`==`1 (##) && (##) acknowledgedBy`==`58].acknowledgedTime.count"; //test data
			
			Pattern moduleNamePattern = Pattern.compile(EXPRESSION_FORMATTER);
			Matcher moduleMatcher = moduleNamePattern.matcher(expressionString);
			
			String criteriaString = null;
			while(moduleMatcher.find()) {
				if(moduleMatcher.group(2) != null && !moduleMatcher.group(2).trim().equals("")) {
					moduleName = moduleMatcher.group(2).trim();
				}
				if(moduleMatcher.group(3) != null && !moduleMatcher.group(3).trim().equals("")) {
					criteriaString = moduleMatcher.group(3).trim();
				}
				if(moduleMatcher.group(4) != null && !moduleMatcher.group(4).trim().equals("")) {
					selectField = moduleMatcher.group(4).trim();
				}
				if(moduleMatcher.group(5) != null && !moduleMatcher.group(5).trim().equals("")) {
					aggregateFunctionString = moduleMatcher.group(5).trim();
				}
				System.out.println("FacilioSubExpressionParser after parsing "+moduleName +" "+criteriaString+" "+selectField+" "+aggregateFunctionString);
			}
			if(moduleName == null || criteriaString == null) {
				throw new Exception("moduleName or criteria cannot be null in expr");
			}
			Pattern condtionStringpattern = Pattern.compile(CONDITION_FORMATTER);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			fields = modBean.getAllFields(moduleName);
			
			Map<String, Condition> conditions = new HashMap<>();
			for (FacilioField field : fields) {
				if(fieldMap == null) {
					fieldMap = new HashMap<>();
				}
				fieldMap.put(field.getName(), field);
			}
			
			String[] values = criteriaString.split(CONDITION_SPACE_SEPERATOR);
			int sequence = 0;
			
			StringBuilder sb =  new StringBuilder();
			for(String value : values) {
				value = value.trim();
				if(value.equals("&&")) {
					sb.append("AND ");
				}
				else if(value.equals("||")) {
					sb.append("OR ");
				}
				else if(value.equals("(")) {
					sb.append("( ");
				}
				else if(value.equals(")")) {
					sb.append(") ");
				}
				else{
					Matcher matcher = condtionStringpattern.matcher(value);
					while (matcher.find()) {
						String fieldName = matcher.group(2);
						FacilioField field = null;
						if(fieldName.equals("id")) {
							field = FieldFactory.getIdField(modBean.getModule(moduleName));
						}
						else {
							field = fieldMap.get(fieldName);
						}
						Operator operator = field.getDataTypeEnum().getOperator(matcher.group(5));
						String conditionValue = matcher.group(6);
						
						Condition condition = null;
						if (matcher.group(3) != null) {
							if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
								BaseLineContext baseLine = BaseLineAPI.getBaseLine(Long.parseLong(matcher.group(4)));
								condition = baseLine.getBaseLineCondition(field, ((DateOperators)operator).getRange(conditionValue));
							}
							else {
								throw new IllegalArgumentException("BaseLine is not supported for this operator");
							}
						}
						else {
							condition = new Condition();
							condition.setField(field);
							condition.setOperator(operator);
							condition.setValue(conditionValue);
						}
						sequence++;
						sb.append(sequence + " ");
						condition.setSequence(sequence);
						conditions.put(String.valueOf(sequence), condition);
					}
				}
			}
			criteria = new Criteria();
			criteria.setConditions(conditions);
			criteria.setPattern(sb.toString());
		}
		
		public Object getResult() throws Exception {
			
			Object subExpresionResult = null;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(modBean.getModule(moduleName).getTableName())
					.andCriteria(criteria);
			
			if(modBean.getModule(moduleName).getExtendModule() != null) {
				selectBuilder.innerJoin(modBean.getModule(moduleName).getExtendModule().getTableName())
				.on(modBean.getModule(moduleName).getTableName()+".ID = "+modBean.getModule(moduleName).getExtendModule().getTableName()+".ID");
			}
			
			if(selectField != null) {
				List<FacilioField> selectFields = new ArrayList<>();
				
				FacilioField select = fieldMap.get(selectField);
				select.setColumnName(select.getExtendedModule().getTableName()+"."+select.getColumnName());
				select.setExtendedModule(null);
				select.setModule(null);
				select.setName(RESULT_STRING);

				if(aggregateFunctionString != null) {
					ExpressionAggregateOperator expAggregateOpp = ExpressionAggregateOperator.getExpressionAggregateOperator(aggregateFunctionString);
					select = expAggregateOpp.getSelectField(select);

					if(expAggregateOpp.equals(ExpressionAggregateOperator.FIRST_VALUE)) {
						selectBuilder.limit(1);
					}
				}
				selectFields.add(select);
				selectBuilder.select(selectFields);
			}
			else {
				selectBuilder.select(fields);
			}
			
			List<Map<String, Object>> props = selectBuilder.get();
			System.out.println("selectBuilder -- "+selectBuilder);
			System.out.println("selectBuilder result -- "+props);
			if(props != null && !props.isEmpty()) {
				if(selectField == null && aggregateFunctionString == null) {
					subExpresionResult = props;
				}
				if(aggregateFunctionString == null) {
					List<Object> returnList = new ArrayList<>(); 
					for(Map<String, Object> prop:props) {
						returnList.add(prop.get(RESULT_STRING));
					}
					subExpresionResult = returnList;
				}
				else {
					subExpresionResult = props.get(0).get(RESULT_STRING);
				}
			}
			System.out.println("EXP -- "+expressionString+" RESULT -- "+subExpresionResult);
			return subExpresionResult;
		}
		
		public boolean isSingleValueReturnTypeExpression() {
			Pattern pattern = Pattern.compile(RETURN_SINGLE_VALUE_EXPRESSION_FORMATTER);
			Matcher matcher = pattern.matcher(expressionString);
			return matcher.find();
		}
		public boolean isListValueReturnTypeExpression() {
			Pattern pattern = Pattern.compile(RETURN_LIST_VALUE_EXPRESSION_FORMATTER);
			Matcher matcher = pattern.matcher(expressionString);
			return matcher.find();
		}
		public boolean isMapValueReturnTypeExpression() {
			Pattern pattern = Pattern.compile(RETURN_MAP_VALUE_EXPRESSION_FORMATTER);
			Matcher matcher = pattern.matcher(expressionString);
			return matcher.find();
		}
		public String expressionString;
		
		public String moduleName;
		public String selectField;
		public String aggregateFunctionString;
		public Criteria criteria;
		
		public String getExpressionString() {
			return expressionString;
		}

		public void setExpressionString(String expressionString) {
			this.expressionString = expressionString;
		}
		public String getSelectField() {
			return selectField;
		}

		public void setSelectField(String selectField) {
			this.selectField = selectField;
		}

		public String getAggregateFunctionString() {
			return aggregateFunctionString;
		}

		public void setAggregateFunctionString(String aggregateFunctionString) {
			this.aggregateFunctionString = aggregateFunctionString;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}
		
		public Criteria getCriteria() {
			return criteria;
		}

		public void setCriteria(Criteria criteria) {
			this.criteria = criteria;
		}
		
		List<FacilioField> fields = null;
		Map<String, FacilioField> fieldMap = null;

		public List<FacilioField> getFields() {
			return fields;
		}

		public void setFields(List<FacilioField> fields) {
			this.fields = fields;
		}

		public Map<String, FacilioField> getFieldMap() {
			return fieldMap;
		}

		public void setFieldMap(Map<String, FacilioField> fieldMap) {
			this.fieldMap = fieldMap;
		}
	}	
}
