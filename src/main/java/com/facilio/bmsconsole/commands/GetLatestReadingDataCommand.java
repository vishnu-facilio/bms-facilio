package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class GetLatestReadingDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
			
			List<ReadingInputType> readingInputTypes = new ArrayList<ReadingInputType>();
			String inputType = (String) context.get(FacilioConstants.ContextNames.FILTER);
			ReadingType readingType = null;
			boolean unused = false;
			if (StringUtils.isNotEmpty(inputType)) {
				if (inputType.equals("connected")) {
					readingInputTypes.add(ReadingInputType.CONTROLLER_MAPPED);
					excludeEmptyFields = true;
				}
				else if (inputType.equals("formula")) {
					readingInputTypes.add(ReadingInputType.FORMULA_FIELD);
				}
				else if (inputType.equals("nonformula") || inputType.equals("available")) {
					readingInputTypes = EnumSet.allOf(ReadingInputType.class).stream().filter(type -> type != ReadingInputType.FORMULA_FIELD)
							.collect(Collectors.toList());
					if (inputType.equals("available")) {
						unused = true;
					}
					excludeEmptyFields = false;
				}
				else if (inputType.equals("logged")) {
					readingInputTypes = EnumSet.allOf(ReadingInputType.class).stream().filter(type -> type != ReadingInputType.CONTROLLER_MAPPED && type != ReadingInputType.FORMULA_FIELD)
							.collect(Collectors.toList());
					excludeEmptyFields = true;
				}
				else if (inputType.equals("writable")) {
					readingType = ReadingType.WRITE;
				}
				else if (inputType.equals("readable")) {
					readingType = ReadingType.READ;
				}
			}
			
			List<String> inputTypes = (List<String>) context.get(FacilioConstants.ContextNames.FILTERS);
			if(inputTypes !=null && !inputTypes.isEmpty()){
				for(String inputType1:inputTypes){
					if (inputType1.equals("connected")) {
						readingInputTypes.add(ReadingInputType.CONTROLLER_MAPPED);
						excludeEmptyFields = true;
					}
					else if (inputType1.equals("formula")) {
						readingInputTypes.add(ReadingInputType.FORMULA_FIELD);
					}
					else if (inputType1.equals("nonformula") || inputType1.equals("available")) {
						readingInputTypes = EnumSet.allOf(ReadingInputType.class).stream().filter(type -> type != ReadingInputType.FORMULA_FIELD)
								.collect(Collectors.toList());
						if (inputType.equals("available")) {
							unused = true;
						}
						excludeEmptyFields = false;
					}
					else if (inputType1.equals("logged")) {
						readingInputTypes = EnumSet.allOf(ReadingInputType.class).stream().filter(type -> type != ReadingInputType.CONTROLLER_MAPPED && type != ReadingInputType.FORMULA_FIELD)
								.collect(Collectors.toList());
						excludeEmptyFields = true;
					}
					else if (inputType1.equals("writable")) {
						readingType = ReadingType.WRITE;
					}
					else if (inputType1.equals("readable")) {
						readingType = ReadingType.READ;
					}
				}
			}
			
			ReadingInputType[] types = null;
			if (!readingInputTypes.isEmpty()) {
				types = readingInputTypes.toArray(new ReadingInputType[readingInputTypes.size()]);
			}
			
			if (fetchCount) {
				long count = ReadingsAPI.getReadingDataMetaCount(parentIds, excludeEmptyFields, unused, search, readingType, types);
				context.put(FacilioConstants.ContextNames.COUNT, count);
				return false;
			}
			
			Map<Long, FacilioField> fieldMap = null;
			long fieldId = (long) context.getOrDefault(FacilioConstants.ContextNames.FIELD_ID, -1l);
			if (fieldId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField(fieldId);
				fieldMap = Collections.singletonMap(fieldId, field);
			}
			
			List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(parentIds, fieldMap, excludeEmptyFields, unused, pagination, search, readingType, types);
			
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
