package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportSetting;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InsertReadingDataMetaForImport extends FacilioCommand {

	private static Logger LOGGER = Logger.getLogger(InsertReadingDataMetaForImport.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {

		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		if (ImportAPI.isResourceExtendedModule(importProcessContext.getModule()) && !(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
			LOGGER.info("UPDATING READING DATA META");
			ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			List<Long> resourceIds = new ArrayList(recordsList.values());
			List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
			ReadingsAPI.updateReadingDataMeta(resources);
			LOGGER.info("READING DATA META UPDATED");
		}
		return false;
	}

}
