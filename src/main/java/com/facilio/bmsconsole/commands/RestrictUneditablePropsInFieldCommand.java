package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class RestrictUneditablePropsInFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField oldField = modBean.getField(field.getFieldId());
		
		FacilioField clonedOldField = oldField.clone();
		
		clonedOldField.setDisplayName(field.getDisplayName());
		
		// set Fieldwise editable properties.
		if(clonedOldField instanceof NumberField) {
			NumberField numberField =(NumberField) field;
			((NumberField) clonedOldField).setUnit(numberField.getUnit());
			((NumberField) clonedOldField).setUnitId(numberField.getUnitId());
			((NumberField) clonedOldField).setMetric(numberField.getMetric());
		}
		else if(clonedOldField instanceof BooleanField) {
			BooleanField booleanField =(BooleanField) field;
			((BooleanField) clonedOldField).setFalseVal(booleanField.getFalseVal());
			((BooleanField) clonedOldField).setTrueVal(booleanField.getTrueVal());
		}
		else if(clonedOldField instanceof EnumField) {
			EnumField enumField =(EnumField) field;
			((EnumField) clonedOldField).setValues(enumField.getValues());
		}
		
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, clonedOldField);
		return false;
	}

}
