package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllAreaCommand implements Command{

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getAllBaseSpaces());
		
		return false;
	}

}
