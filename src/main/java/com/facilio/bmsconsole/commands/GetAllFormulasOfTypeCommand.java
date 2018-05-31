package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllFormulasOfTypeCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldType type = (FormulaFieldType) context.get(FacilioConstants.ContextNames.FORMULA_FIELD_TYPE);
		if (type != null) {
			context.put(FacilioConstants.ContextNames.FORMULA_LIST, FormulaFieldAPI.getAllFormulaFieldsOfType(type));
		}
		else {
			throw new IllegalArgumentException("Type cannot be null");
		}
		return false;
	}

}
