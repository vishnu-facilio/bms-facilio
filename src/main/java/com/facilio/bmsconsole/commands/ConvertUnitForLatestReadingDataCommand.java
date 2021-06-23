package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;

public class ConvertUnitForLatestReadingDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(context.get(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI) != null && (Boolean) context.get(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI)) {
			List<ReadingDataMeta> rdmList = (List<ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
			if (rdmList != null) {
				for(ReadingDataMeta meta : rdmList) {
					ReadingsAPI.convertUnitForRdmData(meta);
				}
			}
		}
		return false;
	}

}
