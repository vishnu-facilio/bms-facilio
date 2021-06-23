package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteSingleDayBusinessHoursCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id=(long)context.get(FacilioConstants.ContextNames.ID);
		
		if(id!=-1){
			BusinessHoursAPI.deleteSingleBusinessHour(id);
		}
		return false;
	}

}
