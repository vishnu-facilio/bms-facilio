package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class GetAllAreaCommand implements Command{

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		context.put(FacilioConstants.ContextNames.AREA_LIST, SpaceAPI.getAllAreas(OrgInfo.getCurrentOrgInfo().getOrgid(), conn));
		
		return false;
	}

}
