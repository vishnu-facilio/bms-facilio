package com.facilio.workflowv2.parser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.autogens.WorkflowV2Parser;
import com.facilio.scriptengine.context.*;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.scriptengine.visitor.CommonParser;
import com.facilio.scriptengine.visitor.FunctionVisitor;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.WorkflowFieldsRelContext;
import com.facilio.workflowv2.contexts.WorkflowModuleRelContext;
import com.facilio.workflowv2.contexts.WorkflowNameSpaceRelContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;


/** This Validator is used to validate script on save(creation of workflow) */
@Getter
@Setter
public class ScriptValidator extends CommonParser<Value> {

    private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(WorkflowUtil.class.getName());
    private WorkflowContext workflowContext;
    private FacilioModule moduleObj;
    private String nameSpace;

    private Value assignmentValue = null;

    private Map<String, Value> varMemoryMap = new HashMap<String, Value>();


    private void putParamValue(String key,Value value) {
        if(key != null) {
            varMemoryMap.put(key, value);
        }
    }

    private Value getParamValue(String key) {
        if(varMemoryMap.containsKey(key)) {
            return varMemoryMap.get(key);
        }
        return null;
    }

    @Override
    public Value visitMapInitialisation(WorkflowV2Parser.MapInitialisationContext ctx) {
        
        return new Value(new HashMap<>());
    }

    @Override
    public Value visitListInitialisation(WorkflowV2Parser.ListInitialisationContext ctx) {
    	return new Value(new ArrayList<>());
    }

    @Override
    public Value visitListInitialisationWithElements(WorkflowV2Parser.ListInitialisationWithElementsContext ctx) {
    	
		List<Object> objects = new ArrayList<>();
        for (WorkflowV2Parser.AtomContext atom : ctx.atom()) {
            Value value = (Value)this.visit(atom);
            objects.add(value.asObject());
        }
        return new Value(objects);
    }

    @Override
    public Value visitAssignSingleVar(WorkflowV2Parser.AssignSingleVarContext ctx) {
        String varName = ctx.VAR().getText();

        Value value = assignmentValue;

        putParamValue(varName, value);

        assignmentValue = null;
        return Value.VOID;
    }

    @Override
    public Value visitAssignSingleBracketVar(WorkflowV2Parser.AssignSingleBracketVarContext ctx) {
        String varName = ctx.VAR().getText();
        Value value = assignmentValue;
        Value parentValue = getParamValue(varName);

        if (parentValue.asObject() instanceof Map) {
            Value key = (Value) this.visit(ctx.expr());
            if (key != null) {
                parentValue.asMap().put(key.asString(), value.asObject());
            }
        }
        assignmentValue = null;
        return Value.VOID;
    }

    @Override
    public Value visitAssignMultiDotVar(WorkflowV2Parser.AssignMultiDotVarContext ctx) {
        boolean isFirst = true;
        Value value = assignmentValue;

        Map parentMap = null;
        int varSize = ctx.VAR().size();
        int i = 0;
        for (TerminalNode var : ctx.VAR()) {

            i++;

            String varName = var.getText();

            if (isFirst) {
                Value parentValue = getParamValue(varName);
                if (parentValue == null) {
                    parentValue = new Value(new HashMap<>());
                    putParamValue(varName, parentValue);
                }

                if (parentValue.asObject() instanceof Map) {
                    parentMap = parentValue.asMap();
                }
                isFirst = false;
                continue;
            }

            if (i == varSize) {
                parentMap.put(varName, value.asObject());
            } else {
                Object currentObject = parentMap.get(varName);

                Map currentMap = null;
                if (currentObject instanceof Map) {
                    currentMap = (Map) currentObject;
                } else {
                    currentMap = new HashMap<>();
                    parentMap.put(varName, currentMap);
                }
                parentMap = currentMap;
            }
        }

        assignmentValue = null;

        return Value.VOID;
    }

    @Override
    public Value visitAssignment(WorkflowV2Parser.AssignmentContext ctx) {
    	
    	Value value = this.visit(ctx.expr());
    	
    	this.assignmentValue = value;
    	this.visit(ctx.assignment_var());
    	
        return Value.VOID;
    }

    @Override
    public Value visitVarAtom(WorkflowV2Parser.VarAtomContext ctx) {
        String varName = ctx.getText();
        Value value = getParamValue(varName);
        if (value == null) {
            return Value.VOID;
        }
        return value;
    }

    @Override
    public Value visitStringAtom(WorkflowV2Parser.StringAtomContext ctx) {
       
    	String str = ctx.getText();
        // strip quotes
        str = str.substring(1, str.length() - 1).replace("\"\"", "\"");
        str = str.replace("\\\"", "\"");
        str = str.replace("\\n", "\n");
        str = str.replace("\\r", "\r");
        return new Value(str);
    }

    @Override
    public Value visitNumberAtom(WorkflowV2Parser.NumberAtomContext ctx) {
        if (ctx.getText().contains(".")) {
            return new Value(Double.valueOf(ctx.getText()));
        }
        return new Value(Long.valueOf(ctx.getText()));
    }

    @Override
    public Value visitBooleanAtom(WorkflowV2Parser.BooleanAtomContext ctx) {
    		return new Value(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Value visitNullAtom(WorkflowV2Parser.NullAtomContext ctx) {
        return new Value(null);
    }

    @Override
    public Value visitParanthesisExpr(WorkflowV2Parser.ParanthesisExprContext ctx) {
        
        return new Value(this.visit(ctx.expr()));
    }

    @Override
    public Value visitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx) {
    	try {
    	
			WorkflowModuleRelContext workflowModuleRel=new WorkflowModuleRelContext();
	        String moduleName = ctx.expr().getText();
            char moduleNameStart = moduleName.charAt(0);
            char moduleNameEnd = moduleName.charAt(moduleName.length()-1);

	        moduleName = moduleName.substring(1, moduleName.length()-1);
	        
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(moduleName);
	        if(module == null && (moduleNameStart == '"' && moduleNameEnd=='"')) {
	            throw new ScriptValidationException("Module "+moduleName+ " Does not exist");
	        }
//	        Will be enabled after migration
//	        if(!WorkflowV2Util.scriptWhiteListedModules.contains(moduleName) && !module.isCustom()){
//	            throw new ScriptValidationException("Module action for "+moduleName+ " cannot be done through script");
//	        }
	        setModuleObj(module);
	        workflowModuleRel.setModuleId(module.getModuleId());
	        workflowContext.getModuleRels().add(workflowModuleRel);
	        return new Value(module);
	    }
    	catch(ScriptValidationException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Value.VOID;

    }


    @Override
    public Value visitNewKeywordIntitialization(WorkflowV2Parser.NewKeywordIntitializationContext ctx) {
        String nameSpaceName = ctx.expr(0).getText();
        nameSpaceName = nameSpaceName.substring(1, nameSpaceName.length()-1);
        FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceName);


        if(nameSpaceEnum == null) {
            setNameSpace(nameSpaceName);
            return new Value(nameSpaceName);
        }
        return new Value(nameSpaceEnum);
    }

    @Override
    public Value visitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx) {
        try {
            Value value= (Value)this.visit(ctx.atom());
            for(int i=0;i<ctx.recursive_expression().size();i++) {
                WorkflowV2Parser.Recursive_expressionContext functionCall = ctx.recursive_expression(i);

                if(functionCall.OPEN_PARANTHESIS() != null && functionCall.CLOSE_PARANTHESIS() != null) {
                    String functionName = functionCall.VAR().getText();
                    if (StringUtils.isNotEmpty(nameSpace)) {
                       trackUserFunction(functionName);
                    }
                    if(moduleObj != null){
                        trackModuleFields(functionName, functionCall);
                    }
                }
            }

            return value;
        }
        catch(ScriptValidationException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
		catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return Value.VOID;
    }
    
    private void trackModuleFields(String functionName,WorkflowV2Parser.Recursive_expressionContext functionCall ) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Object moduleFunctionObject = ScriptUtil.getModuleInstanceOf(moduleObj);
        Method method = moduleFunctionObject.getClass().getMethod(functionName, Map.class, List.class, ScriptContext.class);

        Map<String, Integer> writeOperations=new HashMap<>();
        writeOperations.put("add", 0);
        writeOperations.put("update", 1);

        if (method != null && writeOperations.containsKey(functionName)) {
            String paramKey= functionCall.expr(writeOperations.get(functionName)).getText();
            Value paramValue = getParamValue(paramKey);

            if (paramValue != null) {
                Map fields = paramValue.asMap();
                if (fields != null) {
                    Set<String> fieldList = fields.keySet();
                    for (String fieldName : fieldList) {
                        if (fieldName != null) {
                            WorkflowFieldsRelContext fieldsRelContext = new WorkflowFieldsRelContext();
                            FacilioField moduleField = modBean.getField(fieldName, moduleObj.getName());

                            if (moduleField != null) {
                                fieldsRelContext.setFieldId(moduleField.getId());
                                fieldsRelContext.setModuleId(moduleObj.getModuleId());
                                workflowContext.getFieldRels().add(fieldsRelContext);
                            }
                        }
                    }
                }
            }
        }
         setModuleObj(null);
    }

    private void trackUserFunction(String functionName) throws Exception{
        
        WorkflowContext wfContext = null;
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FETCH_SCRIPT_FROM_CACHE)) {
            wfContext = Constants.getScriptBean().getFunction(nameSpace, functionName);
        } else {
            wfContext = UserFunctionAPI.getWorkflowFunction(nameSpace, functionName);
        }
        if (wfContext == null) {
            throw new ScriptValidationException("No such function - " + functionName + ", nameSpace - "+nameSpace) ;
        }
        WorkflowNameSpaceRelContext workflowNameSpaceRel = new WorkflowNameSpaceRelContext();
        workflowNameSpaceRel.setFunctionId(wfContext.getId());
        workflowContext.getNameSpaceRels().add(workflowNameSpaceRel);
        setNameSpace(null);
    }
}
