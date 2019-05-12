package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.BooleanField;
import com.facilio.modules.EnumField;
import com.facilio.modules.FacilioField;
import com.facilio.modules.NumberField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class RestrictUneditablePropsInFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField oldField = modBean.getField(field.getFieldId());
		oldField.setDisplayName(field.getDisplayName());
		
		
		// set Fieldwise editable properties.
		if(oldField instanceof NumberField) {
			NumberField numberField =(NumberField) field;
			((NumberField) oldField).setUnit(numberField.getUnit());
			((NumberField) oldField).setUnitId(numberField.getUnitId());
			((NumberField) oldField).setMetric(numberField.getMetric());
		}
		else if(oldField instanceof BooleanField) {
			BooleanField booleanField =(BooleanField) field;
			((BooleanField) oldField).setFalseVal(booleanField.getFalseVal());
			((BooleanField) oldField).setTrueVal(booleanField.getTrueVal());
		}
		else if(oldField instanceof EnumField) {
			EnumField enumField =(EnumField) field;
			((EnumField) oldField).setValues(enumField.getValues());
		}
		
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, oldField);
		return false;
	}

}
