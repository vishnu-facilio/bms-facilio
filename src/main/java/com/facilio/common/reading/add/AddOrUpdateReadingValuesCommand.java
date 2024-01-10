package com.facilio.common.reading.add;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.common.reading.util.ReadingUtils;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.*;

@Log4j
public class AddOrUpdateReadingValuesCommand extends FacilioCommand {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);

        Boolean updateLastReading = ReadingUtils.getOrDefaultValue(context, FacilioConstants.ContextNames.UPDATE_LAST_READINGS, Boolean.TRUE);
        Boolean adjustTime = ReadingUtils.getOrDefaultValue(context, FacilioConstants.ContextNames.ADJUST_READING_TTIME, Boolean.TRUE);
        Boolean ignoreSplNullHandling = ReadingUtils.getOrDefaultValue(context, FacilioConstants.ContextNames.IGNORE_SPL_NULL_HANDLING, Boolean.FALSE);

        SourceType sourceType = (SourceType) context.get(FacilioConstants.ContextNames.READINGS_SOURCE);
        Set<String> modules = null;
        Map<String, ReadingDataMeta> lastReadingMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        if (MapUtils.isNotEmpty(readingMap)) {
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<String, ReadingDataMeta> currentReadingMap = new HashMap<>();

            if (adjustTime) {
                ReadingsAPI.setReadingInterval(readingMap);
            }

            for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                String moduleName = entry.getKey();
                List<ReadingContext> readings = entry.getValue();
                List<FacilioField> fields = bean.getAllFields(moduleName);
                FacilioModule module = bean.getModule(moduleName);
                List<ReadingContext> readingsToBeAdded = addDefaultPropsAndGetReadingsToBeAdded(module, fields, readings, lastReadingMap, currentReadingMap, adjustTime, updateLastReading, sourceType, ignoreSplNullHandling);
                addReadings(module, fields, readingsToBeAdded, lastReadingMap, currentReadingMap, updateLastReading);
            }
            context.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, currentReadingMap);
            modules = readingMap.keySet();
        }

		context.put(FacilioConstants.ContextNames.RECORD_MAP, readingMap);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

		LOGGER.debug("Time taken to add/update Readings data to DB : " + (System.currentTimeMillis() - startTime) + ", modules : " + modules);
		return false;
    }

    private List<ReadingContext> addDefaultPropsAndGetReadingsToBeAdded(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings, Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean adjustTime, boolean updateLastReading, SourceType sourceType, boolean ignoreSplNullHandling) throws Exception {
        List<ReadingContext> readingsToBeAdded = new ArrayList<>();
        Iterator<ReadingContext> itr = readings.iterator();
        while (itr.hasNext()) {
            ReadingContext reading = itr.next();
            if (reading.getTtime() == -1) {
                reading.setTtime(System.currentTimeMillis());
            }
            if (reading.getParentId() == -1) {
                throw new IllegalArgumentException("Invalid parent id for readings of module : " + module.getName());
            }

            reading.setActualTtime(reading.getTtime());
            if (adjustTime) {
                ReadingUtils.adjustTtime(reading);
            }
            Map<String, Object> readingData = reading.getReadings();
            if (readingData != null && !readingData.isEmpty()) {
                if (reading.getId() == -1) {
                    reading.setNewReading(true);
                    readingsToBeAdded.add(reading);
                }
                reading.setSourceType(sourceType);
                if (module.getName().equals(ContextNames.WEATHER_READING) && readingData.containsKey("temperature")) {
                    LOGGER.debug("Temperature for " + AccountUtil.getCurrentOrg().getId() + ", Ttime - " + reading.getTtime() + ", Parent - " + reading.getParentId() + ", Value - " + readingData.get("temperature") + ", id - " + reading.getId());
                }
            } else {
                itr.remove();
            }
        }
        return readingsToBeAdded;
    }

    private void addReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings,
                             Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean isUpdateLastReading) throws Exception {

        LOGGER.debug(Thread.currentThread().getName() + "Inside addReadings in  AddorUpdateCommand#######  " + readings);

        InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
                .module(module)
                .fields(fields)
                .addRecords(readings);
        readingBuilder.save();
        if (isUpdateLastReading) {
            Map<String, ReadingDataMeta> currentRDMs = ReadingsAPI.updateReadingDataMeta(fields, readings, metaMap);
            if (currentRDMs != null) {
                currentReadingMap.putAll(currentRDMs);
            }
        }

    }

    /*private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading,
                               Map<String, ReadingDataMeta> metaMap, Map<String, ReadingDataMeta> currentReadingMap, boolean isUpdateLastReading, boolean ignoreSplNullHandling) throws Exception {
        LOGGER.debug(Thread.currentThread().getName() + "Inside updateReadings in  AddorUpdateCommand#######  " + reading);

        UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
        if (ignoreSplNullHandling) {
            updateBuilder.ignoreSplNullHandling();
        }
        updateBuilder.update(reading);
        if (isUpdateLastReading) {
            Map<String, ReadingDataMeta> currentRDMs = ReadingsAPI.updateReadingDataMeta(fields, Collections.singletonList(reading), metaMap);
            long lastReadingDataId = -1;
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            for (Map.Entry<String, Object> rd : reading.getReadings().entrySet()) {
                FacilioField fField = fieldMap.get(rd.getKey());
                if (fField != null) {
                    Object val = FacilioUtil.castOrParseValueAsPerType(fField, rd.getValue());
                    if (val != null && metaMap != null) {
                        String uniqueKey = ReadingsAPI.getRDMKey(reading.getParentId(), fField);
                        ReadingDataMeta meta = metaMap.get(uniqueKey);
                        if (meta != null) {
                            lastReadingDataId = meta.getReadingDataId();
                            if ((currentRDMs == null || currentRDMs.isEmpty() || currentRDMs.get(uniqueKey) == null) && reading.getId() == lastReadingDataId) {
                                currentRDMs = ReadingsAPI.updateReadingDataMeta(module, fields, reading, rd.getKey());
                            }
                        }
                    }
                }
            }

            if (MapUtils.isNotEmpty(currentRDMs)) {
                currentReadingMap.putAll(currentRDMs);
            }
        }

        LOGGER.debug(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

    }

     */
}
