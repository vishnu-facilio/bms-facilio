package com.facilio.mv.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.tiles.request.collection.CollectionUtil;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class AddMVBaselineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<MVBaseline> baseLines = null;
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVBaseline baseline = (MVBaseline) context.get(MVUtil.MV_BASELINE);
		
		if(baseline == null) {
			baseLines = mvProjectWrapper.getBaselines();
		}
		else {
			baseLines = Collections.singletonList(baseline);
		}
		
		for(MVBaseline baseLine :baseLines) {										// only one baseline for now
			baseLine.setProject(mvProjectWrapper.getMvProject());
			baseLine.setOrgId(AccountUtil.getCurrentOrg().getId());

			if( baseLine.getFormulaField() != null) {
				context.put(FacilioConstants.ContextNames.FORMULA_FIELD, baseLine.getFormulaField());
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,Collections.singletonList(MVUtil.getMVBaselineReadingField()));
				context.put(FacilioConstants.ContextNames.MODULE,MVUtil.getMVBaselineReadingField().getModule());
				context.put(FacilioConstants.ContextNames.IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V,true);
				
				FormulaFieldContext formulaField = baseLine.getFormulaField();
				
				formulaField.setModule(MVUtil.getMVBaselineReadingField().getModule());
				formulaField.setReadingField(MVUtil.getMVBaselineReadingField());
				
				MVUtil.fillFormulaFieldDetailsForAdd(baseLine.getFormulaField(), mvProjectWrapper.getMvProject(),baseLine,null,context);
				FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain(true);
				addEnpiChain.execute(context);
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
