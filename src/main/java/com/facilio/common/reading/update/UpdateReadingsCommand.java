package com.facilio.common.reading.update;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.common.reading.util.ReadingUtils;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

@Log4j
public class UpdateReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean adjustTime = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.ADJUST_READING_TTIME, Boolean.TRUE);
        Boolean ignoreSplNullHandling = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IGNORE_SPL_NULL_HANDLING, Boolean.FALSE);
        SourceType sourceType = (SourceType) context.get(FacilioConstants.ContextNames.READINGS_SOURCE);
        Map<String, ReadingDataMeta> lastReadingMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(readingMap)) {
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            if (adjustTime) {
                ReadingsAPI.setReadingInterval(readingMap);
            }

            for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                String moduleName = entry.getKey();
                List<ReadingContext> readings = entry.getValue();
                List<FacilioField> fields = bean.getAllFields(moduleName);
                FacilioModule module = bean.getModule(moduleName);
                addDefaultPropsAndUpdateReadings(module, fields, readings, adjustTime, sourceType, ignoreSplNullHandling);
            }
        }
        return false;
    }

    private void addDefaultPropsAndUpdateReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings, boolean adjustTime, SourceType sourceType, boolean ignoreSplNullHandling) throws Exception {
        for (ReadingContext reading : readings) {

            if (reading.getTtime() == -1) {
                throw new IllegalArgumentException("ttime should not be empty while updating the data");
            }
            if (reading.getParentId() == -1) {
                throw new IllegalArgumentException("Invalid parent id for readings of module : " + module.getName());
            }
            if (reading.getId() == -1) {
                throw new IllegalArgumentException("Id cannot be null while updating the readings");
            }

            reading.setActualTtime(reading.getTtime());
            if (adjustTime) {
                ReadingUtils.adjustTtime(reading);
            }

            Map<String, Object> readingData = reading.getReadings();
            if (MapUtils.isNotEmpty(readingData)) {
                reading.setNewReading(false);
                if (sourceType != null) {
                    reading.setSourceType(sourceType);
                }
                updateReading(module, fields, reading, ignoreSplNullHandling);
            }
        }
    }

    private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading, boolean ignoreSplNullHandling) throws Exception {

        UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
                .module(module)
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
        if (ignoreSplNullHandling) {
            updateBuilder.ignoreSplNullHandling();
        }
        updateBuilder.update(reading);
    }
}
