package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadingUnitAndInputConversionCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ReadingUnitAndInputConversionCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
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
										try {
											long orgId = AccountUtil.getCurrentOrg().getId();
											if (orgId == 339l || orgId == 321l) {
												if (moduleName.equals(ContextNames.WEATHER_READING) && fieldName == "temperature") {
													LOGGER.info("Temperature for " + orgId + ", Ttime - " + reading.getTtime() + ", Parent - " + reading.getParentId() + ", Value - " + readingData.get(fieldName) + ", id - " + reading.getId());
												}
											}
											
                                            if (readingDataMeta != null && readingDataMeta.getUnitEnum() != null) {
                                                Object value = UnitsUtil.convertToSiUnit(readingData.get(fieldName), readingDataMeta.getUnitEnum());
                                                readingData.put(fieldName, value);
                                            } else {
                                                Unit unit = (Unit) context.get(FacilioConstants.ContextNames.FORMULA_INPUT_UNIT_STRING);
                                                if (unit != null) {
                                                    Object value = UnitsUtil.convertToSiUnit(readingData.get(fieldName), unit);
                                                    readingData.put(fieldName, value);
                                                }
                                            }
                                        } catch (Exception ex) {
                                            LOGGER.info("unit conversion failed. fieldName : " + fieldName + " , value : " + readingData.get(fieldName) + ", unit : " + readingDataMeta.getUnitEnum() +", field : " + field, ex);
                                            throw ex;
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
		boolean checkField = false;
		if (FacilioProperties.isOnpremise() && (fieldName.equals("liftmode") || fieldName.equals("movingstate")) ) {
			LOGGER.info("-------Data for " + fieldName + ": " + readingData.get(fieldName));
			checkField = true;
		}
		if (rdmValueMap != null && rdmValueMap.get(readingDataMeta.getId()) != null && readingDataMeta.getInputTypeEnum() == ReadingInputType.CONTROLLER_MAPPED) {
			Map<String, Integer> valueMap = rdmValueMap.get(readingDataMeta.getId());
			String value = readingData.get(fieldName).toString();
			if (valueMap != null && valueMap.get(value) != null) {
				readingData.put(fieldName, valueMap.get(value));
			}
			if (checkField) {
				LOGGER.info("Value Map- " + valueMap);
			}
		}
	}
	
	private Map<Long,Map<String, Integer>> getInputValuesMap(Map<String, ReadingDataMeta> metaMap) throws Exception {
		List<Long> rdmIds = metaMap.values().stream().map(ReadingDataMeta::getId).collect(Collectors.toList());
		return ReadingsAPI.getReadingInputValuesMap(rdmIds);
	}

}
