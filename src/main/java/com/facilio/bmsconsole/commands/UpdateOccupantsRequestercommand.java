package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.OccupantsContext;
import com.facilio.bmsconsole.util.OccupantsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateOccupantsRequestercommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<OccupantsContext> occupantsList = (List<OccupantsContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(occupantsList)) {
			for(OccupantsContext occupant : occupantsList) {
				if(occupant.getRequester() != null && occupant.getRequester().getId() > 0) {
					OccupantsAPI.updatePortalUserAccess(occupant, false);
				}
				else {
					if(occupant.isPortalAccessNeeded()) {
						OccupantsAPI.addUserAsRequester(occupant);
					}
				}
			}
		}
		return false;
	}

}
