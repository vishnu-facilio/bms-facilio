package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;

public class GetLatestReadingDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Boolean excludeEmptyFields = (Boolean) context.get(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS);
		if (excludeEmptyFields == null) {
			excludeEmptyFields = false;
		}
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(parentId, null, excludeEmptyFields);
		context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, rdmList);
		return false;
	}

}
