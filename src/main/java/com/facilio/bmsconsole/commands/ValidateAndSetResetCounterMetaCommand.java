package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class ValidateAndSetResetCounterMetaCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        Map<String, ReadingDataMeta> metaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (metaMap == null || metaMap.isEmpty() || readingMap == null || readingMap.isEmpty()) {
            return false;
        }

        List<ResetCounterMetaContext> resetCounterMetaList = new ArrayList<>();
        ResetCounterMetaContext resetCounterMeta;

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {

            String moduleName = entry.getKey();
            List<ReadingContext> readings = entry.getValue();
            List<FacilioField> allFields = bean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

            for (ReadingContext reading : readings) {

                for (Map.Entry<String, Object> readingEntry : reading.getReadings().entrySet()) {

                    String fieldName = readingEntry.getKey();
                    FacilioField field = fieldMap.get(fieldName);

                    if (field == null) { //There'll be fields in ReadingObject which will not have a field entry
                        continue;
                    }

                    boolean isCounterField = ReadingsAPI.isCounterField(field, moduleName);
                    ReadingDataMeta rdm = metaMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
                    boolean resetvalidation = false;
                    if (isCounterField) {
                        if (field.getDataTypeEnum() == FieldType.DECIMAL) {
                            Double readingVal = (Double) FacilioUtil.castOrParseValueAsPerType(field, readingEntry.getValue());
                            if (readingVal != null) {
                                resetvalidation = (Double) rdm.getValue() > readingVal;
                            }
                        } else {
                            Long readingVal = (Long) FacilioUtil.castOrParseValueAsPerType(field, readingEntry.getValue());
                            if (readingVal != null) {
                                resetvalidation = (Long) rdm.getValue() > readingVal;
                            }
                        }

                        if (resetvalidation) {
                            resetCounterMeta = new ResetCounterMetaContext();
                            resetCounterMeta.setFieldId(field.getId());
                            resetCounterMeta.setReadingDataId(reading.getId());//
                            resetCounterMeta.setResourceId(reading.getParentId());
                            resetCounterMeta.setTtime(reading.getTtime());
                            resetCounterMetaList.add(resetCounterMeta);
                        }
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST, resetCounterMetaList);
        }
        LOGGER.debug("ValidateAndSetResetCounterMetaCommand time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }


}
