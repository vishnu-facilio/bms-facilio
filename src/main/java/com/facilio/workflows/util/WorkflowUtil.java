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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddWorkflowRuleCommand;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.ExpressionAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;

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
	static final String CONDITION_STRING =  "condition";
	static final String PATTERN_STRING =  "pattern";
	static final String SEQUENCE_STRING =  "sequence";
	static final String RESULT_STRING =  "result";
	
	
	public static Object getWorkflowExpressionResult(String workflowString,Map<String,Object> paramMap) throws Exception {
		
		workflowString = validateAndFillParameters(workflowString,paramMap);
		WorkflowContext workflowContext = parseStringToWorkflowObject(workflowString);
		return workflowContext.executeWorkflow();
	}
	
	public static Long AddWorkflow(String workflowString) throws Exception {
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflowString);
		return addWorkflow(workflowContext);
	}
	
	public static Long addWorkflow(WorkflowContext workflowContext) throws Exception {

		if(workflowContext.getWorkflowString() == null) {
			workflowContext.setWorkflowString(getXmlStringFromWorkflow(workflowContext));
		}
		
		workflowContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowModule().getTableName())
				.fields(FieldFactory.getWorkflowFields());

		Map<String, Object> props = FieldUtil.getAsProperties(workflowContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		workflowContext.setId((Long) props.get("id"));
		return (Long) props.get("id");
	}
	
	public static WorkflowContext getWorkflowContext(Long workflowId) throws Exception  {
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
		return workflowContext;
	}
	
	public static Object getResult(Long workflowId,Map<String,Object> paramMap)  throws Exception  {
		
		 WorkflowContext workflowContext = getWorkflowContext(workflowId);
		return getWorkflowExpressionResult(workflowContext.getWorkflowString(),paramMap);
	}

	
	public static String validateAndFillParameters(String workflowString,Map<String,Object> paramMap) throws Exception {
		
		List<ParameterContext> paramterContexts = getParameterListFromWorkflowString(workflowString);
		
		if(!paramterContexts.isEmpty()) {
			if(paramMap == null || paramMap.isEmpty()) {
				throw new Exception("No paramters match found");
			}
			if(paramterContexts.size() != paramMap.size()) {
				throw new Exception("No. of arguments mismatched");
			}
			for(ParameterContext parameterContext:paramterContexts) {
				Object value = paramMap.get(parameterContext.getName());
				
				checkType(parameterContext,value);
				parameterContext.setValue(value);
				
				workflowString = replaceParameters(workflowString,parameterContext);
			}
		}
		
		return workflowString;
	}
	
	public static String replaceParameters(String workflowString,ParameterContext parameterContext) {
		
		String variableName = "\\$\\{"+parameterContext.getName()+"\\}";
		
		workflowString = workflowString.replaceAll(variableName, parameterContext.getValue().toString());
		
		return workflowString;
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
					 for(Integer key:conditionMap.keySet()) {
						 
						 Condition condition = conditionMap.get(key);
						 Element conditionElement = doc.createElement(CONDITION_STRING);
						 conditionElement.setAttribute(SEQUENCE_STRING, key.toString());
						 conditionElement.setTextContent(condition.getFieldName()+"`"+condition.getOperator().getOperator()+"`"+condition.getValue());
						 criteriaElement.appendChild(conditionElement);
					 }
					 expressionElement.appendChild(criteriaElement);
					 
					 if(expressionContext.getFieldName() != null) {
						 Element fieldElement = doc.createElement(FIELD_STRING);
						 fieldElement.setAttribute(NAME_STRING, expressionContext.getFieldName());
						 if(expressionContext.getAggregateString() != null) {
							 fieldElement.setAttribute(AGGREGATE_STRING, expressionContext.getAggregateString());
						 }
						 expressionElement.appendChild(fieldElement);
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
		
		InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-8"));
    	
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
	
	public static WorkflowContext parseStringToWorkflowObject(String workflow) throws Exception {
    	
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflow);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-8"));
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
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
        		workflowContext.addParamater(parameterContext);
        	}
        }
        
        NodeList expressionNodes = doc.getElementsByTagName(EXPRESSION_STRING);
        
        for (int i = 0; i < expressionNodes.getLength(); i++) {
        	
        	ExpressionContext expressionContext = new ExpressionContext();
        	
        	 Node expressionNode = expressionNodes.item(i);
             
             if (expressionNode.getNodeType() == Node.ELEMENT_NODE) {
            	 
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
                    			String conditionString = condition.getTextContent();
                    			Pattern condtionStringpattern = Pattern.compile(CONDITION_FORMATTER);
                    			
                    			Matcher matcher = condtionStringpattern.matcher(conditionString);
            					while (matcher.find()) {
            						String fieldName = matcher.group(2);
            						FacilioField field = modBean.getField(fieldName, expressionContext.getModuleName());
            						Operator operator = field.getDataTypeEnum().getOperator(matcher.group(5));
            						String conditionValue = matcher.group(6);
            						
            						if (matcher.group(3) != null) {
            							if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
            								BaseLineContext baseLine = BaseLineAPI.getBaseLine(Long.parseLong(matcher.group(4)));
            								condition1 = baseLine.getBaseLineCondition(field, ((DateOperators)operator).getRange(conditionValue));
            							}
            							else {
            								throw new IllegalArgumentException("BaseLine is not supported for this operator");
            							}
            						}
            						else {
            							condition1 = new Condition();
            							condition1.setField(field);
            							condition1.setOperator(operator);
            							condition1.setValue(conditionValue);
            						}
            					}
                    			conditions.put(Integer.parseInt(sequence), condition1);
                    		}
                    	}
                    	criteria1.setConditions(conditions);
                    	expressionContext.setCriteria(criteria1);
                    }
                }
            }
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
