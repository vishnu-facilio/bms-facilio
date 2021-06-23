package com.facilio.bmsconsole.commands;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetDigestTeamTechnicianCountCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetDigestTeamTechnicianCountCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		LOGGER.log(Level.SEVERE, "startTime -- " + System.currentTimeMillis());

		Map<Long,Object> countMap = WorkOrderAPI.getTechnicianCountBySite();		
		context.put(FacilioConstants.ContextNames.TECH_COUNT_GROUP_DIGEST, countMap);

		return false;
	}


}
