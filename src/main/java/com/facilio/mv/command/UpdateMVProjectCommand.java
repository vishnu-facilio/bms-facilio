package com.facilio.mv.command;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class UpdateMVProjectCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectWrapper mvProjectWrapperOld = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		MVProjectContext mvProjectContext = mvProjectWrapper.getMvProject();
		
		MVProjectContext mvProjectContextOld = mvProjectWrapperOld.getMvProject();
		
		if(mvProjectContext.getSaveGoalFormulaField() != null) {
			if(mvProjectContextOld.getSaveGoalFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, mvProjectContext.getSaveGoalFormulaField());
				MVUtil.fillFormulaFieldDetailsForUpdate(mvProjectContext.getSaveGoalFormulaField(), mvProjectWrapper.getMvProject(),null,null,context);
				
				FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
				updateEnPIChain.execute(context);
			}
			else {
				
				FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
				
				FacilioContext context1 = addEnpiChain.getContext();
				
				context1.put(FacilioConstants.ContextNames.FORMULA_FIELD, mvProjectContext.getSaveGoalFormulaField());
				context1.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVSaveGoalReadingField()));
				context1.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVSaveGoalReadingField().getModule());
				context1.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				
				FormulaFieldContext formulaField = mvProjectContext.getSaveGoalFormulaField();
				
				formulaField.setModule(MVUtil.getMVSaveGoalReadingField().getModule());
				formulaField.setReadingField(MVUtil.getMVSaveGoalReadingField());
				
				MVUtil.fillFormulaFieldDetailsForAdd(mvProjectContext.getSaveGoalFormulaField(), mvProjectWrapper.getMvProject(),null,null,context1);
				
				addEnpiChain.execute();
				
				mvProjectContext.setSaveGoalFormulaField(formulaField);
				
				MVUtil.updateMVProject(mvProjectContext);
			}
		}
		else {
			if(mvProjectContextOld.getSaveGoalFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.RECORD_ID, mvProjectContextOld.getSaveGoalFormulaField().getId());
				context.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				FacilioChain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
				deleteEnPIChain.execute(context);
			}
		}
		
		
		mvProjectWrapper.getMvProject().setIsLocked(Boolean.TRUE);
		MVUtil.updateMVProject(mvProjectWrapper.getMvProject());
		
		return false;
	}


}