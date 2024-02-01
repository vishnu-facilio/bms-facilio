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
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class ReadingUnitConversionCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SourceType readingType = (SourceType) context.get(FacilioConstants.ContextNames.READINGS_SOURCE);
        if(SourceType.IMPORT.equals(readingType)){
            return false;
        }

        long startTime = System.currentTimeMillis();
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        Map<String, ReadingDataMeta> metaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        if (MapUtils.isEmpty(metaMap)) {
            LOGGER.info("meta map is null");
            return false;
        }

        if (MapUtils.isEmpty(readingMap)) {
            LOGGER.info("reading map is null");
            return false;
        }

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
            String moduleName = entry.getKey();
            List<ReadingContext> readings = entry.getValue();
            if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(readings)) {
                List<FacilioField> fields = bean.getAllFields(moduleName);
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                for (ReadingContext reading : readings) {
                    Map<String, Object> readingData = reading.getReadings();
                    if (MapUtils.isNotEmpty(readingData)) {
                        for (String fieldName : readingData.keySet()) {
                            FacilioField field = fieldMap.get(fieldName);
                            if(field == null) {
                                LOGGER.info("field is null for field name : " + fieldName);
                                continue;
                            }
                            if (readingData.get(fieldName) != null) {
                                if (reading.getParentId() > 0 && field.getId() > 0) {
                                    ReadingDataMeta readingDataMeta = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
                                    if (readingDataMeta == null) {
                                        LOGGER.info("Reading data meta is null for parent: " + reading.getParentId() + " for field: " + field);
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
                                        LOGGER.info("unit conversion failed. fieldName : " + fieldName + " , value : " + readingData.get(fieldName) + ", unit : " + readingDataMeta.getUnitEnum() + ", field : " + field, ex);
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

        return false;
    }

}
