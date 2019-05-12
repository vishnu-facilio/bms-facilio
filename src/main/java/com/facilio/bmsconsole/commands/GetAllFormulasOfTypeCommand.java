package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.modules.FacilioModule;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetAllFormulasOfTypeCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FormulaFieldType type = (FormulaFieldType) context.get(FacilioConstants.ContextNames.FORMULA_FIELD_TYPE);
		if (type != null) {
			List<FormulaFieldContext> Formula_List = FormulaFieldAPI.getAllFormulaFieldsOfType(type, true);
			List<FacilioModule> module_id = new ArrayList<>();
			for(FormulaFieldContext formula:Formula_List)
			{
				module_id.add(modBean.getModule(formula.getModuleId()));
			}
			context.put(FacilioConstants.ContextNames.MODULE_LIST,module_id);
			context.put(FacilioConstants.ContextNames.FORMULA_LIST, Formula_List);
		}
		else {
			throw new IllegalArgumentException("Type cannot be null");
		}
		return false;
	}

}
