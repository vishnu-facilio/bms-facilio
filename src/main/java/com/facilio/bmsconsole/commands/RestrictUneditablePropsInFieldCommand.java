package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class RestrictUneditablePropsInFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField oldField = modBean.getField(field.getFieldId());
		oldField.setDisplayName(field.getDisplayName());
		if(oldField instanceof NumberField) {
			NumberField numberField =(NumberField) field;
			((NumberField) oldField).setUnit(numberField.getUnit());
			((NumberField) oldField).setUnitId(numberField.getUnitId());
			((NumberField) oldField).setMetric(numberField.getMetric());
		}
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, oldField);
		return false;
	}

}
