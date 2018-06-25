package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.UnitsUtil;

public class ReadingUnitConversionCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(ReadingUnitConversionCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		Map<String, ReadingDataMeta> metaMap =(Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.READING_DATA_META);
		
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
										String key = reading.getParentId()+"_"+field.getId();
										ReadingDataMeta readingDataMeta = metaMap.get(key);
										if(readingDataMeta.getUnitEnum() != null) {
											Object value = UnitsUtil.convertToSiUnit(readingData.get(fieldName), readingDataMeta.getUnitEnum());
											readingData.put(fieldName, value);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
