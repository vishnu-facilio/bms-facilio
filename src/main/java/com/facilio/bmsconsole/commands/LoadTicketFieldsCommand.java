package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class LoadTicketFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> existingFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(existingFields != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			existingFields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TICKET));
		}
		return false;
	}
}