package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class CreateRollUpFieldDependenciesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = modBean.getModule(moduleName);
		if(moduleName == null || parentModule == null) {
			throw new IllegalArgumentException("Please provide a valid parent Module.");
		}
		
		List<FacilioField> rollUpFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		if(rollUpFields == null || rollUpFields.isEmpty()) {
			throw new IllegalArgumentException("Field is null for the rollup field configuration.");
		}
		
		for(FacilioField rollUpField:rollUpFields) {
			rollUpField.setDisplayType(FacilioField.FieldDisplayType.ROLL_UP_FIELD);
			rollUpField.setDisplayTypeInt(FacilioField.FieldDisplayType.ROLL_UP_FIELD.getIntValForDB());
			rollUpField.setDataType(FieldType.DECIMAL);
			rollUpField.setRequired(false);
			rollUpField.setIsSystemUpdated(true);
		}
		
		return false;
	}

}
