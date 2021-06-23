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
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;

public class ConstructCumulativeConsumptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		List<MVBaseline> baseLines = mvProjectWrapper.getBaselines();
		
		MVProjectWrapper mvProjectWrapperold = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		if(mvProjectWrapperold != null) {
			return false;
		}
		
		StringBuilder workflowString = new StringBuilder();
		
		workflowString.append("Number enpi(Number startTime,Number endTime,Number resourceId,String currentModule,String currentField) {");
		
		for(MVBaseline baseLine :baseLines) {
			
			workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", baseLine.getSavedConsumption().getModuleName()));
			
			String fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT_WITHOUT_START_TIME.replace("${parentId}", baseLine.getSavedConsumption().getResourceId()+"");
			fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(baseLine.getSavedConsumption().getReadingFieldId()).getName());
			
			workflowString.append("A = "+fetchStmt);
			
			workflowString.append("return A;");
			
			workflowString.append("}");
				
			WorkflowContext newBaselineWorkflow = new WorkflowContext();
			newBaselineWorkflow.setWorkflowV2String(workflowString.toString());
			newBaselineWorkflow.setIsV2Script(true);
			
			FormulaFieldContext formulaFieldContext = new FormulaFieldContext();
			formulaFieldContext.setWorkflow(newBaselineWorkflow);
			
			formulaFieldContext.setModule(MVUtil.getMVCumulativeSavedConsumptionField().getModule());
			formulaFieldContext.setReadingField(MVUtil.getMVCumulativeSavedConsumptionField());
			
			FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
			
			FacilioContext context1 = addEnpiChain.getContext();
			
			context1.put(FacilioConstants.ContextNames.FORMULA_FIELD,formulaFieldContext);
			formulaFieldContext.setName(baseLine.getName()+" saved consumption");
			context1.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVCumulativeSavedConsumptionField().getModule());
			context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVCumulativeSavedConsumptionField()));
			context1.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
			MVUtil.fillFormulaFieldDetailsForAdd(formulaFieldContext, mvProjectWrapper.getMvProject(),baseLine,null,context1);
			
			addEnpiChain.execute();
			
			baseLine.setCumulativeSavedConsumption(formulaFieldContext);
			MVUtil.updateMVBaseline(baseLine);
		}
		
		
		
		return false;
	}

}
