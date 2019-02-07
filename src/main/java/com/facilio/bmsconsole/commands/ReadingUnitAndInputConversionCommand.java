package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.UnitsUtil;

public class ReadingUnitAndInputConversionCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(ReadingUnitAndInputConversionCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			Map<Long,Map<String, Integer>> valuesMap = getInputValuesMap(metaMap);
			
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				if (moduleName != null && !moduleName.isEmpty() && readings != null && !readings.isEmpty()) {
					List<FacilioField> fields= bean.getAllFields(moduleName);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
					for (ReadingContext reading : readings) {
						Map<String, Object> readingData = reading.getReadings();
						if (readingData != null && !readingData.isEmpty()) {
							for(String fieldName : readingData.keySet()) {
								FacilioField field = fieldMap.get(fieldName);
								if(field != null && readingData.get(fieldName) != null) {
									if(reading.getParentId() > 0  && field.getId() > 0) {
										ReadingDataMeta readingDataMeta = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
										if(readingDataMeta==null) {
											LOGGER.info("Reading data meta is null for parent: "+reading.getParentId()+" for field: "+field);
										}
										if(readingDataMeta.getUnitEnum() != null) {
											Object value = UnitsUtil.convertToSiUnit(readingData.get(fieldName), readingDataMeta.getUnitEnum());
											readingData.put(fieldName, value);
										}
										convertInputValue(readingDataMeta, valuesMap, readingData, fieldName);
									}
								}
							}
						}
					}
				}
			}
			LOGGER.info("Time taken for Unit conversion for modules : "+readingMap.keySet()+" is "+(System.currentTimeMillis() - startTime));
		}
		return false;
	}
	
	private void convertInputValue(ReadingDataMeta readingDataMeta, Map<Long,Map<String, Integer>> rdmValueMap, Map<String, Object> readingData, String fieldName) {
		if (rdmValueMap != null && rdmValueMap.get(readingDataMeta.getId()) != null && readingDataMeta.getInputTypeEnum() == ReadingInputType.CONTROLLER_MAPPED) {
			Map<String, Integer> valueMap = rdmValueMap.get(readingDataMeta.getId());
			Object value = readingData.get(fieldName);
			if (valueMap != null && valueMap.get(value) != null) {
				readingData.put(fieldName, valueMap.get(value));
			}
		}
	}
	
	private Map<Long,Map<String, Integer>> getInputValuesMap(Map<String, ReadingDataMeta> metaMap) throws Exception {
		List<Long> rdmIds = metaMap.values().stream().map(ReadingDataMeta::getId).collect(Collectors.toList());
		return ReadingsAPI.getReadingInputValuesMap(rdmIds);
	}

}
