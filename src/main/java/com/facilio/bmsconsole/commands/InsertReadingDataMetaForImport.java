package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InsertReadingDataMetaForImport implements Command {

	private static Logger LOGGER = Logger.getLogger(UpdateBaseAndResourceCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		LOGGER.severe("UPDATING READING DATA META");
		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		List<Long> resourceIds = new ArrayList(recordsList.values());
		List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
		ReadingsAPI.updateReadingDataMeta(resources);
		LOGGER.severe("READING DATA META UPDATED");
		return false;
	}

}
