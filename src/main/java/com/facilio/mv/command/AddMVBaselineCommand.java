package com.facilio.mv.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.tiles.request.collection.CollectionUtil;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;
import com.facilio.time.DateRange;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddMVBaselineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<MVBaseline> baseLines = null;
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVBaseline baseline = (MVBaseline) context.get(MVUtil.MV_BASELINE);
		
		if(baseline == null) {
			baseLines = mvProjectWrapper.getBaselines();
		}
		else {
			baseLines = Collections.singletonList(baseline);
		}
		
		for(MVBaseline baseLine :baseLines) {
			baseLine.setProject(mvProjectWrapper.getMvProject());
			baseLine.setOrgId(AccountUtil.getCurrentOrg().getId());
			if( baseLine.getFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseLine.getFormulaField());
				MVUtil.fillFormulaFieldDetailsForAdd(baseLine.getFormulaField(), mvProjectWrapper.getMvProject(),baseLine,null,context);
				Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
				addEnpiChain.execute(context);
			}
			else if(baseLine.getWorkflow() != null) {
				
				context.put(WorkflowV2Util.WORKFLOW_CONTEXT, baseLine.getWorkflow());
				Chain addWorkflowChain =  TransactionChainFactory.getAddWorkflowChain(); 
				addWorkflowChain.execute(context);
			}
		}
		
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_BASELINE_MODULE);
		
		InsertRecordBuilder<MVBaseline> insert = new InsertRecordBuilder<MVBaseline>()
				.module(module)
				.fields(fields)
				.addRecords(baseLines);
		
		insert.save();
		
		return false;
	}

}
