package com.facilio.mv.command;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;

public class ConstructBaselineFormulaWithAdjustmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		List<MVBaseline> baseLines = mvProjectWrapper.getBaselines();
		
		List<MVAdjustment> adjustments =  mvProjectWrapper.getAdjustments();
		
		for(MVBaseline baseLine :baseLines) {
			
			MVBaseline oldBaseline = MVUtil.getMVBaseline(baseLine.getId());
			
			if(oldBaseline != null && oldBaseline.getFormulaFieldWithAjustment() != null && oldBaseline.getFormulaFieldWithAjustment().getId() > 0) {
				FormulaFieldContext formulaField = FormulaFieldAPI.getFormulaField(oldBaseline.getFormulaFieldWithAjustment().getId());
				baseLine.setFormulaFieldWithAjustment(formulaField);
			}
			
			StringBuilder workflowString = new StringBuilder();
			
			StringBuilder resultStringBuilder = new StringBuilder();
			
			workflowString.append("Number enpi(Number startTime,Number endTime,Number resourceId,String currentModule,String currentField) {");
			
			FormulaFieldContext formulaField = baseLine.getFormulaField();
			
			formulaField = FormulaFieldAPI.getFormulaField(formulaField.getId());
			baseLine.setFormulaField(formulaField);
			
			workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(formulaField.getModuleId()).getName()));
			
			String fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", formulaField.getResourceId()+"");
			fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(formulaField.getReadingFieldId()).getName());
			
			workflowString.append("A = "+fetchStmt);
			
			workflowString.append("if(A == null) { return null; }") ;
			
			resultStringBuilder.append("A");
			
			char varName = 'B';
			for(MVAdjustment adjustment :adjustments) {
				
				formulaField = adjustment.getFormulaField();
				
				if(formulaField != null) {
					formulaField = FormulaFieldAPI.getFormulaField(formulaField.getId());
					adjustment.setFormulaField(formulaField);
					
					workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(formulaField.getModuleId()).getName()));
					
					fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", formulaField.getResourceId()+"");
					fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(formulaField.getReadingFieldId()).getName());
					
					workflowString.append(varName+" = "+fetchStmt);
				}
				else {
					workflowString.append(varName+" = 0;");
					workflowString.append(MVUtil.WORKLFOW_ADJ_DATE_RANGE_CHECK.replace("${startTime}", adjustment.getStartTime()+"").replace("${endTime}", adjustment.getEndTime()+""));
					workflowString.append(varName+" = "+adjustment.getConstant()+";");
					workflowString.append("}");
				}
				workflowString.append(MVUtil.WORKLFOW_VALUE_NULL_CHECK_STMT.replaceAll(Pattern.quote("${var}"), varName+""));
				resultStringBuilder.append("+"+varName);
				varName++;
			}
			
			workflowString.append("return "+resultStringBuilder+"; }");
			
			WorkflowContext newBaselineWorkflow = new WorkflowContext();
			newBaselineWorkflow.setWorkflowV2String(workflowString.toString());
			newBaselineWorkflow.setIsV2Script(true);
			
			if(baseLine.getFormulaFieldWithAjustment() != null) {
				
				FormulaFieldContext formulaFieldContext = baseLine.getFormulaFieldWithAjustment();
				formulaFieldContext.setWorkflow(newBaselineWorkflow);
				MVUtil.fillFormulaFieldDetailsForUpdate(formulaFieldContext, mvProjectWrapper.getMvProject(),baseLine,null,context);
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formulaFieldContext);
				
				FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
				updateEnPIChain.execute(context);
				
				baseLine.setFormulaFieldWithAjustment(FormulaFieldAPI.getFormulaField(formulaFieldContext.getId()));
				
			}
			else {
				FormulaFieldContext formulaFieldContext = new FormulaFieldContext();
				formulaFieldContext.setWorkflow(newBaselineWorkflow);
				
				formulaFieldContext.setModule(MVUtil.getMVBaselineAdjustmentReadingField().getModule());
				formulaFieldContext.setReadingField(MVUtil.getMVBaselineAdjustmentReadingField());
				
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD,formulaFieldContext);
				formulaFieldContext.setName(baseLine.getName()+"WithAjustment");
				context.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVBaselineAdjustmentReadingField().getModule());
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVBaselineAdjustmentReadingField()));
				context.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				MVUtil.fillFormulaFieldDetailsForAdd(formulaFieldContext, mvProjectWrapper.getMvProject(),baseLine,null,context);
				
				FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
				addEnpiChain.execute(context);
				
				baseLine.setFormulaFieldWithAjustment(formulaFieldContext);
				MVUtil.updateMVBaseline(baseLine);
			}
		}
		
		return false;
	}
}
