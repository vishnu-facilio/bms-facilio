package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateRDMCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(UpdateReadingDataMetaCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReadingDataMeta> metaList = (List<ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
		if (metaList != null && !metaList.isEmpty()) {
			
			for(ReadingDataMeta meta :metaList) {
				ReadingsAPI.updateReadingDataMeta(meta);
			}
		}
		return false;
	}
	
}
