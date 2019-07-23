package com.facilio.mv.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class AddMVAdjustmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<MVAdjustment> adjustments = null;
		
		MVAdjustment adjustment = (MVAdjustment) context.get(MVUtil.MV_ADJUSTMENT);
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		if(adjustment == null) {
			adjustments = mvProjectWrapper.getAdjustments();
		}
		else {
			adjustments = Collections.singletonList(adjustment);
		}
		
		for(MVAdjustment adjustment1 :adjustments) {
			adjustment1.setProject(mvProjectWrapper.getMvProject());
			adjustment1.setOrgId(AccountUtil.getCurrentOrg().getId());
			if(adjustment1.getFormulaField() != null) {
				MVUtil.fillFormulaFieldDetailsForAdd(adjustment1.getFormulaField(), mvProjectWrapper.getMvProject(),null,adjustment1,context);
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, adjustment1.getFormulaField());

				Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
				addEnpiChain.execute(context);
			}
		}
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modbean.getModule(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.MV_ADJUSTMENT_MODULE);
		
		InsertRecordBuilder<MVAdjustment> insert = new InsertRecordBuilder<MVAdjustment>()
				.module(module)
				.fields(fields)
				.addRecords(adjustments);
		
		insert.save();
		
		return false;
	}

}