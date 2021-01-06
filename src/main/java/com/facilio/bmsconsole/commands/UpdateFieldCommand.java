package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.MultiLookupField;

public class UpdateFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioField field = (FacilioField) context.get(FacilioConstants.ContextNames.MODULE_FIELD);
		boolean avoidFieldDisplayNameDuplication = (boolean) context.getOrDefault(ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, false);
		if (avoidFieldDisplayNameDuplication) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField updateField = modBean.getFieldFromDB(field.getFieldId());
			List<FacilioField> facilioFields = modBean.getAllFields(updateField.getModule().getName());
			for(FacilioField facilioField : facilioFields) {
				if (facilioField.getDisplayName().equals(field.getDisplayName()) && facilioField.getFieldId() != field.getFieldId()) {
					throw new IllegalArgumentException("Field Display Name Duplication Is Not Allowed");
				}
			}
			if (field instanceof MultiEnumField) {
				((MultiEnumField)field).setRelModule(((MultiEnumField)updateField).getRelModule());
				((MultiEnumField)field).setRelModuleId(((MultiEnumField)updateField).getRelModuleId());
			} else if(field instanceof MultiLookupField) {
				((MultiLookupField)field).setRelModule(((MultiLookupField)updateField).getRelModule());
				((MultiLookupField)field).setRelModuleId(((MultiLookupField)updateField).getRelModuleId());
			}
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, modBean.updateField(field));
		return false;
	}

}
