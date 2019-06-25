package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;

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
			
			Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
			if (fetchCount == null) {
				fetchCount = false;
			}
			String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
			JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
			
			List<ReadingInputType> readingTypes = new ArrayList<ReadingInputType>();
			String readingType = (String) context.get(FacilioConstants.ContextNames.FILTER);
			if (StringUtils.isNotEmpty(readingType)) {
				if (readingType.equals("connected")) {
					readingTypes.add(ReadingInputType.CONTROLLER_MAPPED);
				}
				if (readingType.equals("formula")) {
					readingTypes.add(ReadingInputType.FORMULA_FIELD);
				}
				else if (readingType.equals("nonformula")) {
					readingTypes = EnumSet.allOf(ReadingInputType.class).stream().filter(type -> type != ReadingInputType.FORMULA_FIELD)
							.collect(Collectors.toList());
				}
			}
			
			ReadingInputType[] types = null;
			if (!readingTypes.isEmpty()) {
				types = (ReadingInputType[]) readingTypes.toArray(new ReadingInputType[readingTypes.size()]);
			}
			
			if (fetchCount) {
				long count = ReadingsAPI.getReadingDataMetaCount(parentIds, excludeEmptyFields, search, types);
				context.put(FacilioConstants.ContextNames.COUNT, count);
				return false;
			}
			
			Map<Long, FacilioField> fieldMap = (Map<Long, FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_MAP);
			
			List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(parentIds, fieldMap, excludeEmptyFields, pagination, search, types);
			
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
