package com.facilio.workflows.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;

public class WorkflowUtil {

	static final List<String> COMPARISION_OPPERATORS = new ArrayList<>();
	static final List<String> ARITHMETIC_OPPERATORS = new ArrayList<>();
	
	private static final String CONDITION_FORMATTER = "((.*?)`(baseLine\\{(\\d+)\\}\\s*)?([^`]*)`(.*))";
	
	static {
		COMPARISION_OPPERATORS.add("==");
		COMPARISION_OPPERATORS.add("=");
		COMPARISION_OPPERATORS.add("!=");
		COMPARISION_OPPERATORS.add("<>");
		COMPARISION_OPPERATORS.add("<");
		COMPARISION_OPPERATORS.add(">");
		COMPARISION_OPPERATORS.add(">=");
		COMPARISION_OPPERATORS.add("<=");
		COMPARISION_OPPERATORS.add("&&");
		COMPARISION_OPPERATORS.add("||");
		
		ARITHMETIC_OPPERATORS.add("+");
		ARITHMETIC_OPPERATORS.add("-");
		ARITHMETIC_OPPERATORS.add("*");
		ARITHMETIC_OPPERATORS.add("/");
		ARITHMETIC_OPPERATORS.add("%");
		ARITHMETIC_OPPERATORS.add("^");
	}
	
	public static List<String> getComparisionOpperator() {
		return COMPARISION_OPPERATORS;
	}
	public static List<String> getArithmeticOpperator() {
		return ARITHMETIC_OPPERATORS;
	}
	static final String WORKFLOW_STRING =  "workflow";
	static final String PARAMETER_STRING =  "parameter";
	static final String TYPE_STRING =  "type";
	static final String EXPRESSION_STRING =  "expression";
	static final String NAME_STRING =  "name";
	static final String CONSTANT_STRING =  "constant";
	static final String MODULE_STRING =  "module";
	static final String FIELD_STRING =  "field";
	static final String AGGREGATE_STRING =  "aggregate";
	static final String CRITERIA_STRING =  "criteria";
	static final String ORDER_BY_STRING =  "orderBy";
	static final String SORT_STRING =  "sort";
	static final String LIMIT_STRING =  "limit";
	static final String CONDITION_STRING =  "condition";
	static final String CONDITIONS_STRING =  "conditions";
	static final String PATTERN_STRING =  "pattern";
	static final String SEQUENCE_STRING =  "sequence";
	static final String RESULT_STRING =  "result";
	
	
	public static Object getWorkflowExpressionResult(String workflowString,Map<String,Object> paramMap) throws Exception {
		
		WorkflowContext workflowContext = parseStringToWorkflowObject(workflowString);
		List<ParameterContext> parameterContexts = validateAndGetParameters(workflowContext.getParameters(),paramMap);
		workflowContext.setParameters(parameterContexts);
		return workflowContext.executeWorkflow();
	}
	
	public static Map<String, Object> getExpressionResultMap(String workflowString,Map<String,Object> paramMap) throws Exception {
		
		WorkflowContext workflowContext = parseStringToWorkflowObject(workflowString);
		List<ParameterContext> parameterContexts = validateAndGetParameters(workflowContext.getParameters(),paramMap);
		workflowContext.setParameters(parameterContexts);
		workflowContext.executeWorkflow();
		return workflowContext.getVariableResultMap();
	}
	
	public static Map<String, Object> getExpressionResultMap(Long workflowId,Map<String,Object> paramMap)  throws Exception  {
		
		 WorkflowContext workflowContext = getWorkflowContext(workflowId);
		return getExpressionResultMap(workflowContext.getWorkflowString(),paramMap);
	}
	
	public static Long addWorkflow(String workflowString) throws Exception {
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflowString);
		return addWorkflow(workflowContext);
	}
	
	public static void deleteWorkflow(long id) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		deleteBuilder.delete();
	}
	
	public static Long addWorkflow(WorkflowContext workflowContext) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		WorkflowContext workflow = new WorkflowContext();
		if(workflowContext.getWorkflowString() == null) {
			workflow.setWorkflowString(getXmlStringFromWorkflow(workflowContext));
		}
		else {
			workflow.setWorkflowString(workflowContext.getWorkflowString());
		}
		
		workflow.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		System.out.println("ADDING WORKFLOW STRING--- "+workflowContext.getWorkflowString());
		
		System.out.println("ADDING WORKFLOW STRING--- "+workflowContext.getWorkflowString());
		
		workflowContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowModule().getTableName())
				.fields(FieldFactory.getWorkflowFields());

		Map<String, Object> props = FieldUtil.getAsProperties(workflow);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		workflowContext.setId((Long) props.get("id"));
		
		insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowFieldModule().getTableName())
				.fields(FieldFactory.getWorkflowFieldsFields());
		for(ExpressionContext expression :workflowContext.getExpressions()) {
			
			String fieldName = expression.getFieldName();
			String moduleName = expression.getModuleName();
			
			if(moduleName != null && fieldName != null) {
				System.out.println("moduleName -- "+moduleName +" fieldName -- "+fieldName);
				FacilioModule module = modBean.getModule(moduleName);
				FacilioField field = modBean.getField(fieldName, moduleName);
				if(field != null) {
					WorkflowFieldContext workflowFieldContext = new WorkflowFieldContext();
					
					workflowFieldContext.setOrgId(module.getOrgId());
					workflowFieldContext.setModuleId(module.getModuleId());
					workflowFieldContext.setFieldId(field.getId());
					workflowFieldContext.setWorkflowId(workflowContext.getId());
					props = FieldUtil.getAsProperties(workflowFieldContext);
					insertBuilder.addRecord(props);
					System.out.println("ADDED WORKFLOW FIELD ID --- "+(Long) props.get("id"));
				}
			}
		}
		insertBuilder.save();
		return workflowContext.getId();
	}
	
	public static List<WorkflowFieldContext> getWorkflowFields(long workflowId) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowFieldModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFieldsFields();
		FacilioField workflowIdField = FieldFactory.getAsMap(fields).get("workflowId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(workflowIdField, String.valueOf(workflowId), PickListOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<WorkflowFieldContext> workflowFields = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				workflowFields.add(FieldUtil.getAsBeanFromMap(prop, WorkflowFieldContext.class));
			}
			return workflowFields;
		}
		return null;
	}
	
	public static WorkflowContext getWorkflowContext(Long workflowId) throws Exception  {
		return getWorkflowContext(workflowId,false);
	}
	public static WorkflowContext getWorkflowContext(Long workflowId,boolean isWithExpParsed) throws Exception  {
		
		FacilioModule module = ModuleFactory.getWorkflowModule(); 
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(ModuleFactory.getWorkflowModule().getTableName()+".ID = ?", workflowId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowContext.class);
		}
		workflowContext = parseStringToWorkflowObject(workflowContext.getWorkflowString());
		
		if(isWithExpParsed) {
			List<ExpressionContext> temp= new ArrayList<>();
			if(workflowContext.getExpressions() != null) {
				for(ExpressionContext expressionContext:workflowContext.getExpressions()) {
					expressionContext = getExpressionContextFromExpressionString(expressionContext.getExpressionString());
					temp.add(expressionContext);
				}
				workflowContext.setExpressions(temp);
			}
		}
		
		return workflowContext;
	}
	
	public static Object getResult(Long workflowId,Map<String,Object> paramMap)  throws Exception  {
		
		 WorkflowContext workflowContext = getWorkflowContext(workflowId);
		return getWorkflowExpressionResult(workflowContext.getWorkflowString(),paramMap);
	}
	
	public static List<ParameterContext> validateAndGetParameters(List<ParameterContext> paramterContexts,Map<String,Object> paramMap) throws Exception {
		
		if(!paramterContexts.isEmpty()) {
			if(paramMap == null || paramMap.isEmpty()) {
				throw new Exception("No paramters match found");
			}
			if(paramterContexts.size() > paramMap.size()) {
				throw new Exception("No. of arguments mismatched");
			}
			for(ParameterContext parameterContext:paramterContexts) {
				Object value = paramMap.get(parameterContext.getName());
				
				checkType(parameterContext,value);
				
				parameterContext.setValue(value);
			}
		}
		return paramterContexts;
		
	}
	
	public static boolean checkType(ParameterContext parameterContext,Object value) throws Exception {
		
		FieldType type =  parameterContext.getType();
		
		switch(type) {
			case STRING : 
				if(value instanceof String) {
					return true;
				}
				throw new Exception(parameterContext.getName()+" type mismatched "+value);
			case BOOLEAN:
				if(value instanceof Boolean) {
					return true;
				}
				throw new Exception(parameterContext.getName()+" type mismatched "+value);
			case NUMBER:
				if(value instanceof Integer || value instanceof Long) {
					return true;
				}
				throw new Exception(parameterContext.getName()+" type mismatched "+value);
			case DECIMAL:
				if(value instanceof Double) {
					return true;
				}
				throw new Exception(parameterContext.getName()+" type mismatched "+value);
		}
		return false;
	}
	
	public static String getXmlStringFromWorkflow(WorkflowContext workflowContext) throws Exception {
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.newDocument();
		 
		 Element workflowElement = doc.createElement(WORKFLOW_STRING);
		 
		 if(workflowContext.getParameters() != null) {
			 
			 for(ParameterContext parameterContext : workflowContext.getParameters()) {
				 Element parameterElement = doc.createElement(PARAMETER_STRING);
				 parameterElement.setAttribute(NAME_STRING, parameterContext.getName());
				 parameterElement.setAttribute(TYPE_STRING, parameterContext.getTypeString());
				 workflowElement.appendChild(parameterElement);
			 }
		 }

		 if(workflowContext.getExpressions() != null) {
			 
			 for(ExpressionContext expressionContext:workflowContext.getExpressions()) {
				 
				 Element expressionElement = doc.createElement(EXPRESSION_STRING);
				 expressionElement.setAttribute(NAME_STRING, expressionContext.getName());
				 
				 if(expressionContext.getConstant() != null) {
					 Element valueElement = doc.createElement(CONSTANT_STRING);
					 valueElement.setTextContent(expressionContext.getConstant().toString());
					 expressionElement.appendChild(valueElement);
				 }
				 else {
					 Element moduleElement = doc.createElement(MODULE_STRING);
					 moduleElement.setAttribute(NAME_STRING, expressionContext.getModuleName());
					 expressionElement.appendChild(moduleElement);
					 
					 
					 Element criteriaElement = doc.createElement(CRITERIA_STRING);
					 criteriaElement.setAttribute(PATTERN_STRING, expressionContext.getCriteria().getPattern());
					 
					 Map<Integer, Condition> conditionMap =  expressionContext.getCriteria().getConditions();
					 for(Map.Entry<Integer, Condition> conditionEntry : conditionMap.entrySet()) {
						 
						 Condition condition = conditionEntry.getValue();
						 Object key = conditionEntry.getKey();
						 Element conditionElement = doc.createElement(CONDITION_STRING);
						 conditionElement.setAttribute(SEQUENCE_STRING, key.toString());
						 if (condition.getOperator() instanceof DateOperators && expressionContext.getConditionSeqVsBaselineId() != null &&  expressionContext.getConditionSeqVsBaselineId().containsKey(key)) {
							 conditionElement.setTextContent(condition.getFieldName()+"`baseLine{" + expressionContext.getConditionSeqVsBaselineId().get(key) + "}"+condition.getOperator().getOperator()+"`"+condition.getValue());
						 }
						 else {
							 conditionElement.setTextContent(condition.getFieldName()+"`"+condition.getOperator().getOperator()+"`"+condition.getValue());
						 }
						 criteriaElement.appendChild(conditionElement);
					 }
					 expressionElement.appendChild(criteriaElement);
					 
					 if(expressionContext.getFieldName() != null) {
						 Element fieldElement = doc.createElement(FIELD_STRING);
						 fieldElement.setAttribute(NAME_STRING, expressionContext.getFieldName());
						 if(expressionContext.getAggregateString() != null) {
							 fieldElement.setAttribute(AGGREGATE_STRING, expressionContext.getAggregateString());
						 }
						 
						 if(expressionContext.getAggregateCondition() != null) {
							 for(Condition condition : expressionContext.getAggregateCondition()) {
								 
								 Element conditionElement = doc.createElement(CONDITION_STRING);
								 conditionElement.setTextContent(condition.getFieldName()+"`"+condition.getOperator().getOperator()+"`"+condition.getValue());
								 fieldElement.appendChild(conditionElement);
							 }
						 }
						 
						 expressionElement.appendChild(fieldElement);
					 }
					 if(expressionContext.getOrderByFieldName() != null) {
						 Element orderElement = doc.createElement(ORDER_BY_STRING);
						 orderElement.setAttribute(NAME_STRING, expressionContext.getOrderByFieldName());
						 if(expressionContext.getSortBy() != null) {
							 orderElement.setAttribute(SORT_STRING, expressionContext.getSortBy());
						 }
						 expressionElement.appendChild(orderElement);
					 }
					 if(expressionContext.getLimit() != null) {
						 Element limitElement = doc.createElement(LIMIT_STRING);
						 limitElement.setTextContent(expressionContext.getLimit());
						 expressionElement.appendChild(limitElement);
					 }
				 }
				 workflowElement.appendChild(expressionElement);
			 }
			 
			 if(workflowContext.getResultEvaluator() != null) {
				 Element resultElement = doc.createElement(RESULT_STRING);
				 resultElement.setTextContent(workflowContext.getResultEvaluator());
				 workflowElement.appendChild(resultElement);
			 }
		 }
		 
		 doc.appendChild(workflowElement);
		 
		 DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		 LSSerializer lsSerializer = domImplementation.createLSSerializer();
		 
		 String result = lsSerializer.writeToString(doc);
		 System.out.println("result -- "+result);
		 return result;
	}
	public static List<ParameterContext> getParameterListFromWorkflowString(String workflow) throws Exception {
		
		InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-16"));
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
        List<ParameterContext> paramterContexts = new ArrayList<>();
        NodeList parameterNodes = doc.getElementsByTagName(PARAMETER_STRING);
        for(int i=0;i<parameterNodes.getLength();i++) {
        	
        	Node parameterNode = parameterNodes.item(i);
        	if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
        		ParameterContext parameterContext = new ParameterContext();
        		Element parameter = (Element) parameterNode;
        		
        		String name = parameter.getAttribute(NAME_STRING);
        		String type = parameter.getAttribute(TYPE_STRING);
        		parameterContext.setName(name);
        		parameterContext.setTypeString(type);
        		
        		paramterContexts.add(parameterContext);
        	}
        }
        return paramterContexts;
	}
	
	public static ExpressionContext getExpressionContextFromExpressionString(String expressionString) throws Exception {
		
		InputStream stream = new ByteArrayInputStream(expressionString.getBytes("UTF-16"));
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
        NodeList expressionNodes = doc.getElementsByTagName(EXPRESSION_STRING);
        Node expressionNode = expressionNodes.item(0);
		
		if (expressionNode.getNodeType() == Node.ELEMENT_NODE) {
			
			ExpressionContext expressionContext = new ExpressionContext();
			expressionContext.setExpressionString(expressionString);
       	 
            Element expression = (Element) expressionNode;
            String expressionName = expression.getAttribute(NAME_STRING);
            
            expressionContext.setName(expressionName);
            
            NodeList valueNodes = expression.getElementsByTagName(CONSTANT_STRING);
            
            if(valueNodes.getLength() > 0 ) {
            	Node valueNode =  valueNodes.item(0);
            	if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
            		Element value = (Element) valueNode;
            		String valueString = value.getTextContent();
            		expressionContext.setConstant(valueString);
            	}
            }
            else {
            	NodeList moduleNodes = expression.getElementsByTagName(MODULE_STRING);
                Node moduleNode = moduleNodes.item(0);
                if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
                	 Element module = (Element) moduleNode;
                	 String moduleName = module.getAttribute(NAME_STRING);
                	 expressionContext.setModuleName(moduleName);
                }
                
                NodeList fieldNodes = expression.getElementsByTagName(FIELD_STRING);
                if(fieldNodes.getLength() > 0) {
                    Node fieldNode = fieldNodes.item(0);
                    if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                    	 
                    	 Element field = (Element) fieldNode;
    	            	 String fieldName = field.getAttribute(NAME_STRING);
    	            	 expressionContext.setFieldName(fieldName);
    	            	 String aggregate = field.getAttribute(AGGREGATE_STRING);
    	            	 expressionContext.setAggregateString(aggregate);
    	            	 
    	            	 NodeList aggreagteConditionNodes = field.getElementsByTagName(CONDITION_STRING);
    	            	 for(int j=0;j<aggreagteConditionNodes.getLength();j++) {
    	            		 
    	            		 Node aggreagteConditionNode = aggreagteConditionNodes.item(j); 
    	            		 if (aggreagteConditionNode.getNodeType() == Node.ELEMENT_NODE) {
    	            			 Element condition = (Element) aggreagteConditionNode;
    	            			 Condition condition1 = getConditionObjectFromConditionString(expressionContext,condition.getTextContent(),expressionContext.getModuleName(),null);
    	            			 expressionContext.addAggregateCondition(condition1);
    	            		 }
    	            	 }
                    }
                }
                
                NodeList criteriaNodes = expression.getElementsByTagName(CRITERIA_STRING);
                Node criteriaNode = criteriaNodes.item(0);
                if (criteriaNode.getNodeType() == Node.ELEMENT_NODE) {
                	Element criteria  = (Element) criteriaNode;
                	String pattern = criteria.getAttribute(PATTERN_STRING);
                	Criteria criteria1 = new Criteria();
                	criteria1.setPattern(pattern);
                	Map<Integer, Condition> conditions = new HashMap<>();
                	NodeList conditionNodes = criteria.getElementsByTagName(CONDITION_STRING);
                	for(int j=0;j<conditionNodes.getLength();j++) {
                		Condition condition1 = null;
                		Node conditionNode = conditionNodes.item(j);
                		if(conditionNode.getNodeType() == Node.ELEMENT_NODE) {
                			Element condition  = (Element) conditionNode;
                			String sequence = condition.getAttribute(SEQUENCE_STRING);
                			condition1 = getConditionObjectFromConditionString(expressionContext,condition.getTextContent(),expressionContext.getModuleName(),Integer.parseInt(sequence));
                			conditions.put(Integer.parseInt(sequence), condition1);
                		}
                	}
                	criteria1.setConditions(conditions);
                	expressionContext.setCriteria(criteria1);
                }
                
                NodeList orderByNodes = expression.getElementsByTagName(ORDER_BY_STRING);
                Node orderByNode = orderByNodes.item(0);
               
                if(orderByNode != null && orderByNode.getNodeType() == Node.ELEMENT_NODE) {
                	Element orderBy  = (Element) orderByNode;
                	String orderByFieldName = orderBy.getAttribute(NAME_STRING);
                	String sortString = orderBy.getAttribute(SORT_STRING);
                	expressionContext.setOrderByFieldName(orderByFieldName);
                	expressionContext.setSortBy(sortString);
                }
                NodeList limitNodes = expression.getElementsByTagName(LIMIT_STRING);
                Node limitNode = limitNodes.item(0);
                
                if(limitNode != null && limitNode.getNodeType() == Node.ELEMENT_NODE) {
                	
                	Element limit  = (Element) limitNode;
                	expressionContext.setLimit(limit.getTextContent());
                }
            }
            return expressionContext;
        }
		return null;
	}
	
	private static Condition getConditionObjectFromConditionString(ExpressionContext expressionContext,String conditionString,String moduleName,Integer sequence) throws Exception {

		Pattern condtionStringpattern = Pattern.compile(CONDITION_FORMATTER);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Matcher matcher = condtionStringpattern.matcher(conditionString);
		Condition condition = null;
		while (matcher.find()) {
			String fieldName = matcher.group(2);
			FacilioField field = modBean.getField(fieldName, moduleName);
			Operator operator = field.getDataTypeEnum().getOperator(matcher.group(5));
			String conditionValue = matcher.group(6);
			
			condition = new Condition();
			
			boolean isWithParamCondition = false;
			String testString = conditionString+" test";
			if(testString.split(WorkflowContext.VARIABLE_PLACE_HOLDER).length > 1) {
				isWithParamCondition = true;
			}
			if (matcher.group(3) != null && !isWithParamCondition) {
				if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
					BaseLineContext baseLine = BaseLineAPI.getBaseLine(Long.parseLong(matcher.group(4)));
					condition = baseLine.getBaseLineCondition(field, ((DateOperators)operator).getRange(conditionValue));
					if(sequence != null) {
						expressionContext.addConditionSeqVsBaselineId(sequence, baseLine.getId());
					}
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
				if(matcher.group(3) != null && sequence != null) {
					
					if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
						if(matcher.group(4) != null) {
							expressionContext.addConditionSeqVsBaselineId(sequence,Long.parseLong(matcher.group(4)));
						}
					}
				}
			}
		}
		return condition;
	}
	
	public static WorkflowContext parseStringToWorkflowObject(String workflow) throws Exception {
    	
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflow);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-16"));
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
        workflowContext.setParameters(getParameterListFromWorkflowString(workflow));
        
        
        NodeList expressionNodes = doc.getElementsByTagName(EXPRESSION_STRING);
        
        for (int i = 0; i < expressionNodes.getLength(); i++) {
        	
        	ExpressionContext expressionContext = new ExpressionContext();
        	
        	Node expressionNode = expressionNodes.item(i);
        	 
        	Document document = expressionNode.getOwnerDocument();
        	DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        	LSSerializer serializer = domImplLS.createLSSerializer();
        	String str = serializer.writeToString(expressionNode);
        	expressionContext.setExpressionString(str);
             
            workflowContext.addExpression(expressionContext);
        }
        
        NodeList resultNodes = doc.getElementsByTagName(RESULT_STRING);
        if(resultNodes.getLength() > 0) {
        	Node resultNode = resultNodes.item(0);
        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element result  = (Element) resultNode;
        		String resultString = result.getTextContent();
        		workflowContext.setResultEvaluator(resultString);
        	}
        }
        return workflowContext;
	}
}
