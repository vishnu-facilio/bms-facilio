package com.facilio.mv.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVAdjustmentVsBaseline;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowContext.WorkflowUIMode;

public class ConstructBaselineFromulaWithAjustmentCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		List<MVBaseline> baseLines = null;
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		MVBaseline baseline = (MVBaseline) context.get(MVUtil.MV_BASELINE);
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		if(baseline == null) {
			baseLines = mvProjectWrapper.getBaselines();
		}
		else {
			baseLines = Collections.singletonList(baseline);
		}
		
		List<MVAdjustment> adjustments =  mvProjectWrapper.getAdjustments();
		List<MVAdjustmentVsBaseline> ajustmentsVsBaselineContexts = new ArrayList<>();
		for(MVAdjustment adjustment :adjustments) {
			ajustmentsVsBaselineContexts.addAll(adjustment.getAdjustmentVsBaseline());
		}
		
		Map<Long, MVAdjustment> adjustmentMap = getAjustmentMap(mvProjectWrapper);
		
		for(MVBaseline baseLine :baseLines) {
			
			StringBuilder workflowString = new StringBuilder();
			
			StringBuilder resultStringBuilder = new StringBuilder();
			
			workflowString.append("void enpi() {");
			
			FormulaFieldContext formulaField = baseLine.getFormulaField();
			
			workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(formulaField.getModuleId()).getName()));
			
			String fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", formulaField.getResourceId()+"");
			fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(formulaField.getReadingFieldId()).getName());
			
			workflowString.append("A = "+fetchStmt);
			
			resultStringBuilder.append("A");
			
			char varName = 'B';
			for(MVAdjustmentVsBaseline ajustmentsVsBaselineContext :ajustmentsVsBaselineContexts) {
				
				if(ajustmentsVsBaselineContext.getBaselineId() == baseLine.getId()) {
					
					formulaField = adjustmentMap.get(ajustmentsVsBaselineContext.getAdjustmentId()).getFormulaField();
					workflowString.append(MVUtil.WORKLFOW_MODULE_INITITALIZATION_STMT.replace("${moduleName}", modbean.getModule(formulaField.getModuleId()).getName()));
					
					fetchStmt = MVUtil.WORKLFOW_VALUE_FETCH_STMT.replace("${parentId}", formulaField.getResourceId()+"");
					fetchStmt = fetchStmt.replace("${fieldName}", modbean.getField(formulaField.getReadingFieldId()).getName());
					
					workflowString.append(varName+" = "+fetchStmt);
					resultStringBuilder.append("+"+varName);
					varName++;
				}
			}
			
			workflowString.append("return "+resultStringBuilder+"; }");
			
			WorkflowContext newBaselineWorkflow = new WorkflowContext();
			newBaselineWorkflow.setWorkflowV2String(workflowString.toString());
			newBaselineWorkflow.setWorkflowUIMode(WorkflowUIMode.NEW_WORKFLOW);
			
			FormulaFieldContext formulaFieldContext = new FormulaFieldContext();
			formulaFieldContext.setWorkflow(newBaselineWorkflow);
			
			context.put(FacilioConstants.ContextNames.FORMULA_FIELD,formulaFieldContext);
			formulaFieldContext.setName(baseLine.getName()+"WithAjustment");
			MVUtil.fillFormulaFieldDetails(formulaFieldContext, mvProjectWrapper.getMvProject(),null,null);
			Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
			addEnpiChain.execute(context);
			
			baseLine.setFormulaFieldWithAjustment(formulaFieldContext);
			
			MVUtil.updateMVBaseline(baseLine);
		}
		
		return false;
	}

	
	Map<Long,MVAdjustment> getAjustmentMap(MVProjectWrapper mvProject) {
		
		List<MVAdjustment> adjustments = mvProject.getAdjustments();
		Map<Long,MVAdjustment> adjustmentNameMap = new HashMap<>();
		if(adjustments != null) {
			for(MVAdjustment adjustment :adjustments) {
				adjustmentNameMap.put(adjustment.getId(), adjustment);
			}
		}
		return adjustmentNameMap;
	}
}
