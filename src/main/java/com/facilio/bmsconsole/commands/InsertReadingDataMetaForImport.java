package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;

public class InsertReadingDataMetaForImport implements Command {

	private static Logger LOGGER = Logger.getLogger(UpdateBaseAndResourceCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		LOGGER.severe("UPDATING READING DATA META");
		ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		List<Long> resourceIds = new ArrayList(recordsList.values());
		System.out.println(resourceIds);
		List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
		for( ResourceContext resource : resources) {
			System.out.println(resource.getName());
			List<ResourceContext> singleResource = new ArrayList<>();
			singleResource.add(resource);
			ReadingsAPI.updateReadingDataMeta(singleResource);
		}
		LOGGER.severe("READING DATA META UPDATED");
		return false;
	}

}
