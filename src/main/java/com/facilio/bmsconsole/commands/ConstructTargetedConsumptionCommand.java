package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;

public class ConstructTargetedConsumptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		List<MVBaseline> baseLines = mvProjectWrapper.getBaselines();
		
		MVProjectContext project = mvProjectWrapper.getMvProject();
		
		StringBuilder workflowString = new StringBuilder();
		
		workflowString.append("Number enpi(Number startTime,Number endTime,Number resourceId,String currentModule,String currentField) {");
		
		for(MVBaseline baseLine :baseLines) {
			
			workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(baseLine.getFormulaFieldWithAjustment().getModuleId()).getName()));
			
			String fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", baseLine.getFormulaFieldWithAjustment().getResourceId()+"");
			fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(baseLine.getFormulaFieldWithAjustment().getReadingFieldId()).getName());
			
			workflowString.append("A = "+fetchStmt);
			
			workflowString.append("if(A == null) { return null; }");
			
			if(project.getSaveGoal() > 0) {
				
				workflowString.append("saveGoal = "+project.getSaveGoal()+";");
				workflowString.append("return A*saveGoal/100;");
			}
			else if (project.getSaveGoalFormulaField() != null) {
				
				workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(project.getSaveGoalFormulaField().getModuleId()).getName()));
				
				fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", project.getSaveGoalFormulaField().getResourceId()+"");
				fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(project.getSaveGoalFormulaField().getReadingFieldId()).getName());
				
				workflowString.append("B = "+fetchStmt);
				
				workflowString.append("if(B == null) { return null; }");
				
				workflowString.append("return A-B;");
				
			}
			else {
				workflowString.append("return null;");
			}
			
			workflowString.append("}");
			
			WorkflowContext newBaselineWorkflow = new WorkflowContext();
			newBaselineWorkflow.setWorkflowV2String(workflowString.toString());
			newBaselineWorkflow.setIsV2Script(true);
			
			MVBaseline oldBaseline = MVUtil.getMVBaseline(baseLine.getId());
			
			if(oldBaseline != null && oldBaseline.getTargetConsumption() != null) {
				
				FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
				
				FacilioContext context1 = updateEnPIChain.getContext();
				
				FormulaFieldContext formulaFieldContext = oldBaseline.getTargetConsumption();
				formulaFieldContext.setWorkflow(newBaselineWorkflow);
				MVUtil.fillFormulaFieldDetailsForUpdate(formulaFieldContext, mvProjectWrapper.getMvProject(),baseLine,null,context1);
				context1.put(FacilioConstants.ContextNames.FORMULA_FIELD, formulaFieldContext);
				
				updateEnPIChain.execute();
				
			}
			else {
				FormulaFieldContext formulaFieldContext = new FormulaFieldContext();
				formulaFieldContext.setWorkflow(newBaselineWorkflow);
				
				formulaFieldContext.setModule(MVUtil.getMVTargetConsumptionField().getModule());
				formulaFieldContext.setReadingField(MVUtil.getMVTargetConsumptionField());
				
				FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
				
				FacilioContext context1 = addEnpiChain.getContext();
				
				context1.put(FacilioConstants.ContextNames.FORMULA_FIELD,formulaFieldContext);
				formulaFieldContext.setName(baseLine.getName()+" target consumption");
				context1.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVTargetConsumptionField().getModule());
				context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVTargetConsumptionField()));
				context1.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				MVUtil.fillFormulaFieldDetailsForAdd(formulaFieldContext, mvProjectWrapper.getMvProject(),baseLine,null,context1);
				
				addEnpiChain.execute();
				
				baseLine.setTargetConsumption(formulaFieldContext);
				MVUtil.updateMVBaseline(baseLine);
			}
		}
		
		return false;
	}

}
