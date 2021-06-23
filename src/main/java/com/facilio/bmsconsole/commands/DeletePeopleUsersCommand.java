package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class DeletePeopleUsersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isNotEmpty(recordIds)) {
			for(Long id : recordIds) {
				PeopleAPI.deletePeopleUsers(id);
			}
		}
		return false;
	}

}
