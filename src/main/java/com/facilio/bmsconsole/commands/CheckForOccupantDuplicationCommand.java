package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.OccupantsContext;
import com.facilio.bmsconsole.util.OccupantsAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForOccupantDuplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<OccupantsContext> occupants = (List<OccupantsContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(occupants)) {
			for(OccupantsContext occupant : occupants) {
			   if(OccupantsAPI.checkForDuplicateOccupant(occupant)) {
				   throw new IllegalArgumentException("An occupant with the same email already exists");
			   }
			}
		}
		return false;
	}

}
