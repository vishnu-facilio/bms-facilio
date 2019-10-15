package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class GetAllFormulasOfTypeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldType type = (FormulaFieldType) context.get(FacilioConstants.ContextNames.FORMULA_FIELD_TYPE);
		if (type != null) {
			List<FormulaFieldContext> formulas = FormulaFieldAPI.getAllFormulaFieldsOfType(type, true);
			if (formulas == null) {
				return false;
			}
			List<FacilioModule> readings = formulas.stream().map(formula -> formula.getModule()).collect(Collectors.toList());
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
			context.put(FacilioConstants.ContextNames.FORMULA_LIST, formulas);
		}
		else {
			throw new IllegalArgumentException("Type cannot be null");
		}
		return false;
	}

}
