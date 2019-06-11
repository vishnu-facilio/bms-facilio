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
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class UpdateMVBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProject mvProject = (MVProject) context.get(MVUtil.MV_PROJECT);
		MVProject mvProjectOld = (MVProject) context.get(MVUtil.MV_PROJECT_OLD);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		List<MVBaseline> baselines = mvProject.getBaselines();
		
		List<MVBaseline> oldBaselines = mvProjectOld.getBaselines();
		
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
					// add here
					context.put(MVUtil.MV_BASELINE, baseline);
					Chain chain = TransactionChainFactory.getAddMVBaselineChain();
					chain.execute(context);
				}
				else {
					// update here
					
					context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseline.getFormulaField());
					
					Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
					updateEnPIChain.execute(context);
					
					UpdateRecordBuilder<MVBaseline> update = new UpdateRecordBuilder<MVBaseline>()
							.module(module)
							.fields(fields)
							.andCondition(CriteriaAPI.getIdCondition(baseline.getId(), module));
					
					update.update(baseline);
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
