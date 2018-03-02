package com.facilio.workflows.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.context.ExpressionContext;
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
	
	static final String EXPRESSION_STRING =  "expression";
	static final String NAME_STRING =  "name";
	static final String VALUE_STRING =  "value";
	static final String MODULE_STRING =  "module";
	static final String FIELD_STRING =  "field";
	static final String AGGREGATE_STRING =  "aggregate";
	static final String CRITERIA_STRING =  "criteria";
	static final String CONDITION_STRING =  "condition";
	static final String PATTERN_STRING =  "pattern";
	static final String SEQUENCE_STRING =  "sequence";
	static final String RESULT_STRING =  "result";
	
	public static Object getWorkflowExpressionResult(String workflow) throws Exception {
		return parseStringToWorkflowObject(workflow).getResult();
	}
	public static WorkflowContext parseStringToWorkflowObject(String workflow) throws Exception {
    	
		WorkflowContext workflowContext = new WorkflowContext();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-8"));
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        
        NodeList expressionNodes = doc.getElementsByTagName(EXPRESSION_STRING);
        
        for (int i = 0; i < expressionNodes.getLength(); i++) {
        	
        	ExpressionContext expressionContext = new ExpressionContext();
        	
        	 Node expressionNode = expressionNodes.item(i);
             
             if (expressionNode.getNodeType() == Node.ELEMENT_NODE) {
            	 
                Element expression = (Element) expressionNode;
                String expressionName = expression.getAttribute(NAME_STRING);
                
                expressionContext.setName(expressionName);
                
                NodeList valueNodes = expression.getElementsByTagName(VALUE_STRING);
                
                if(valueNodes.getLength() > 0 ) {
                	Node valueNode =  valueNodes.item(0);
                	if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
                		Element value = (Element) valueNode;
                		String valueString = value.getTextContent();
                		expressionContext.setValue(valueString);
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
