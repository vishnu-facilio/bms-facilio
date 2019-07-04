package com.facilio.mv.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class UpdateMVBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
					Chain chain = TransactionChainFactory.getAddMVBaselineChain();
					chain.execute(context);
				}
				else {
					if(baseline.getFormulaField() != null) {
						context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseline.getFormulaField());
						MVUtil.fillFormulaFieldDetailsForUpdate(baseline.getFormulaField(), mvProjectWrapper.getMvProject(),baseline,null,context);
						Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
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
