package com.facilio.bmsconsole.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		Collection<Long> parentIds = (Collection<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds == null || parentIds.isEmpty()) {
			Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			if (parentId != null) {
				parentIds = Collections.singletonList(parentId);
			}
		}
		if (parentIds != null && !parentIds.isEmpty()) {
			List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(parentIds, null, excludeEmptyFields);
			Boolean fetchInputValues = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES);
			if (rdmList != null && fetchInputValues != null && fetchInputValues) {
				List<Long> rdmIds = rdmList.stream().map(ReadingDataMeta::getId).collect(Collectors.toList());
				Map<Long, Map<Integer, String>> valuesMap = ReadingsAPI.getReadingIdxVsValuesMap(rdmIds);
				if (valuesMap != null) {
					rdmList.forEach(rdm -> rdm.setInputValues(valuesMap.get(rdm.getId())));
				}
			}
				
			context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, rdmList);
		}
		
		return false;
	}

}
