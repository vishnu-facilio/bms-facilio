package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;

public class LoadTicketFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> existingFields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(existingFields != null) {
			existingFields.addAll(FieldUtil.getAllFields(FacilioConstants.ContextNames.TICKET, ((FacilioContext)context).getConnectionWithoutTransaction()));
		}
		return false;
	}

}
