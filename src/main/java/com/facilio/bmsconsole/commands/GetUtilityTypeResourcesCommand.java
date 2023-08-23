package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class GetUtilityTypeResourcesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetUtilityTypeResourcesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {

		LOGGER.info("Inside GetUtilityTypeResourcesCommand");
		long utilityType=(long)context.get(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID);
			
		List <? extends V3ResourceContext> resourceList= null;
		resourceList= MetersAPI.getMeterListOfUtilityType(utilityType);

		if (resourceList != null) {
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, resourceList.stream().map(V3ResourceContext::getId).collect(Collectors.toList()));
		}
		
		return false;
	}

}
