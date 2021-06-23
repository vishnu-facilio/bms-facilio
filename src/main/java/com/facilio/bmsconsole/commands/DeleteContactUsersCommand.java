package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteContactUsersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> contactIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isNotEmpty(contactIds)) {
			for(Long id : contactIds) {
				ContactsAPI.deleteContactUser(id);
			}
		}
		return false;
	}

}
