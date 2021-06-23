package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class LoadTicketFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> existingFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(existingFields != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			existingFields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TICKET));
		}
		return false;
	}
}