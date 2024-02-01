package com.facilio.common.reading.add;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.UnitsUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class ReadingUnitConversionToDisplayUnit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        SourceType readingType = (SourceType) context.get(FacilioConstants.ContextNames.READINGS_SOURCE);
        if(SourceType.IMPORT.equals(readingType)){
            return false;
        }

        // TODO Auto-generated method stub
        long startTime = System.currentTimeMillis();
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        Map<String, ReadingDataMeta> metaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        Map<String, ReadingDataMeta> currentReadingMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META);

        if (MapUtils.isNotEmpty(readingMap)) {
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                String moduleName = entry.getKey();
                List<ReadingContext> readings = entry.getValue();
                if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(readings)) {
                    List<FacilioField> fields = bean.getAllFields(moduleName);
                    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                    for (ReadingContext reading : readings) {
                        Map<String, Object> readingData = reading.getReadings();
                        if (MapUtils.isNotEmpty(readingMap)) {
                            for (String fieldName : readingData.keySet()) {
                                FacilioField field = fieldMap.get(fieldName);
                                if (field != null && readingData.get(fieldName) != null) {
                                    if (reading.getParentId() > 0 && field.getId() > 0) {
                                        ReadingDataMeta readingDataMeta = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
                                        if (readingDataMeta == null) {
                                            LOGGER.debug("Reading data meta is null for parent: " + reading.getParentId() + " for field: " + field);
                                        }
                                        Object convertedValue = null;
                                        if (field instanceof NumberField) {
                                            NumberField numberField = (NumberField) field;
                                            convertedValue = UnitsUtil.convertToDisplayUnit(readingData.get(fieldName), numberField);
                                            readingData.put(fieldName, convertedValue);
                                        }

                                        if (MapUtils.isNotEmpty(currentReadingMap)) {
                                            ReadingDataMeta currentReadingDataMeta = currentReadingMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
                                            if (currentReadingDataMeta != null && currentReadingDataMeta.getValue() != null && currentReadingDataMeta.getField() != null && currentReadingDataMeta.getField() instanceof NumberField && !currentReadingDataMeta.getValue().equals("-1.0")) {
                                                if (convertedValue != null) {
                                                    currentReadingDataMeta.setValue(convertedValue);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //LOGGER.info("Time taken for Unit conversion for modules : "+readingMap.keySet()+" is "+(System.currentTimeMillis() - startTime));
        }
        LOGGER.debug("ReadingUnitConversionToDisplayUnit time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }
}
