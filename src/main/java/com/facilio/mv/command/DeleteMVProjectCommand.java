package com.facilio.mv.command;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class DeleteMVProjectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectContext mvProject = mvProjectWrapper.getMvProject();
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_PROJECT_MODULE);
		
		MVProjectWrapper wrapperOld = MVUtil.getMVProject(mvProject.getId());
		
		DeleteRecordBuilder<MVProjectContext> delete = new DeleteRecordBuilder<MVProjectContext>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(mvProject.getId(), module));
		
		delete.delete();
		
		deleteDependencies(wrapperOld,context);
		return false;
	}

	private void deleteDependencies(MVProjectWrapper wrapper,Context context) throws Exception {
		for(MVBaseline baseline : wrapper.getBaselines()) {
			context.put(FacilioConstants.ContextNames.RECORD_ID, baseline.getFormulaField().getId());
			
			Chain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
			deleteEnPIChain.execute(context);
			
			Chain deleteEnPIChain1 = FacilioChainFactory.deleteFormulaChain();
			context.put(FacilioConstants.ContextNames.RECORD_ID, baseline.getFormulaFieldWithAjustment().getId());
			
			deleteEnPIChain1.execute(context);
		}
		if(wrapper.getAdjustments() != null && !wrapper.getAdjustments().isEmpty()) {
			for(MVAdjustment adjustment : wrapper.getAdjustments()) {
				if(adjustment.getFormulaField() != null) {
					context.put(FacilioConstants.ContextNames.RECORD_ID, adjustment.getFormulaField().getId());
					
					Chain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
					deleteEnPIChain.execute(context);
				}
			}
		}
	}


}
