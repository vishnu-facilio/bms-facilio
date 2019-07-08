package com.facilio.mv.command;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class UpdateMVPojectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectWrapper mvProjectWrapperOld = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		MVProjectContext mvProjectContext = mvProjectWrapper.getMvProject();
		
		MVProjectContext mvProjectContextOld = mvProjectWrapperOld.getMvProject();
		
		if(mvProjectContext.getSaveGoalFormulaField() != null) {
			if(mvProjectContextOld.getSaveGoalFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, mvProjectContext.getSaveGoalFormulaField());
				MVUtil.fillFormulaFieldDetailsForUpdate(mvProjectContext.getSaveGoalFormulaField(), mvProjectWrapper.getMvProject(),null,null,context);
				
				Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
				updateEnPIChain.execute(context);
			}
			else {
				MVUtil.fillFormulaFieldDetailsForAdd(mvProjectContext.getSaveGoalFormulaField(), mvProjectWrapper.getMvProject(),null,null,context);
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, mvProjectContext.getSaveGoalFormulaField());

				Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
				addEnpiChain.execute(context);
			}
		}
		else {
			if(mvProjectContextOld.getSaveGoalFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.RECORD_ID, mvProjectContextOld.getSaveGoalFormulaField().getId());
				
				Chain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
				deleteEnPIChain.execute(context);
			}
		}
		
		
		mvProjectWrapper.getMvProject().setIsLocked(Boolean.TRUE);
		MVUtil.updateMVProject(mvProjectWrapper.getMvProject());
		
		return false;
	}


}