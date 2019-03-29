package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UpdateReadingDataMetaCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(UpdateReadingDataMetaCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<ReadingDataMeta> metaList = (List<ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
		if (metaList != null && !metaList.isEmpty()) {
			ReadingInputType type = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
			List<ReadingDataMeta> toBeUpdatedList = new ArrayList<>();
			for (ReadingDataMeta meta : metaList) {
				if (meta.getInputTypeEnum() != type) {
					toBeUpdatedList.add(meta);
				}
			}
			if (!toBeUpdatedList.isEmpty()) {
				ReadingsAPI.updateReadingDataMetaInputType(toBeUpdatedList, type, null);
			}
		}
		return false;
	}
	
}
