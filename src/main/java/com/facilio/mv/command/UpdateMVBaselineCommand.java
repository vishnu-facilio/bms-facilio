package com.facilio.mv.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class UpdateMVBaselineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectWrapper mvProjectWrapperOld = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER_OLD);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		List<MVBaseline> baselines = mvProjectWrapper.getBaselines();
		
		List<MVBaseline> oldBaselines = mvProjectWrapperOld.getBaselines();
		
		List<MVBaseline> deletedBaselines = new ArrayList<MVBaseline>();
		
		for(MVBaseline oldBaseline :oldBaselines) {
			
			boolean foundFlag = false;
			for(MVBaseline baseline :baselines) {
				if(oldBaseline.getId() == baseline.getId()) {
					foundFlag = true;
					break;
				}
			}
			if(!foundFlag) {
				deletedBaselines.add(oldBaseline);
			}
		}
		
		if(baselines != null) {
			
			for(MVBaseline baseline :baselines) {
				
				if(baseline.getId() < 0) {
					context.put(MVUtil.MV_BASELINE, baseline);
					FacilioChain chain = TransactionChainFactory.getAddMVBaselineChain();
					chain.execute(context);
				}
				else {
					if(baseline.getFormulaField() != null) {
						context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseline.getFormulaField());
						MVUtil.fillFormulaFieldDetailsForUpdate(baseline.getFormulaField(), mvProjectWrapper.getMvProject(),baseline,null,context);
						FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
						updateEnPIChain.execute(context);
					}
					
					MVUtil.updateMVBaseline(baseline);
				}
			}
		}
		
		for(MVBaseline deletedBaseline :deletedBaselines) {
			
			DeleteRecordBuilder<MVBaseline> delete = new DeleteRecordBuilder<MVBaseline>()
					.module(module)
					.andCondition(CriteriaAPI.getIdCondition(deletedBaseline.getId(), module));
			
			delete.delete();
			
		}
		return false;
	}


}
