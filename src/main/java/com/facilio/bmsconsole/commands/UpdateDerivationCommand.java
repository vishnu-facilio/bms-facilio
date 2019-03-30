package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.DerivationContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.DerivationAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateDerivationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		DerivationContext derivationContext = (DerivationContext) context.get(FacilioConstants.ContextNames.DERIVATION);
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formula != null) {
			derivationContext.setFormulaId(formula.getId());
		}
		DerivationAPI.updateDerivation(derivationContext);
		
		return false;
	}

}
