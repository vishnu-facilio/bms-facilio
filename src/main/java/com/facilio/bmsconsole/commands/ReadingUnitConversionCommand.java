package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
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

public class ReadingUnitConversionCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(ReadingUnitConversionCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		if(metaMap == null || metaMap.isEmpty()) {
			return false;
		}

		if (readingMap != null && !readingMap.isEmpty()) {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

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
									}
								}
							}
						}
					}
				}
			}
            LOGGER.debug("Time taken for Unit conversion is : " + (System.currentTimeMillis() - startTime) + ", modules: " + readingMap.keySet());
		}
		return false;
	}

}
