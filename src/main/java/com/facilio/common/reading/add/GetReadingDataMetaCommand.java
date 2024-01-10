package com.facilio.common.reading.add;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class GetReadingDataMetaCommand extends FacilioCommand {
    private static final int BATCH_SIZE = 100;

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean skipRDM = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_PREV_READING_DATA_META, false);
        if (skipRDM) {
            context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, new HashMap<String, ReadingDataMeta>());
            return false;
        }

        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        long startTime = System.currentTimeMillis();
        if (MapUtils.isNotEmpty(readingMap)) {
            Map<String, ReadingDataMeta> readingDataMeta = null;
            ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
            long parentId = -1;
            long readingFieldId = -1;
            for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
                String moduleName = entry.getKey();
                List<ReadingContext> readings = entry.getValue();
                List<FacilioField> allFields = bean.getAllFields(moduleName);
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

                for (ReadingContext reading : readings) {
                    Map<String, Object> readingData = reading.getReadings();
                    if (MapUtils.isNotEmpty(readingMap)) {
                        parentId = reading.getParentId();
                        for (String fieldName : readingData.keySet()) {
                            FacilioField field = fieldMap.get(fieldName);
                            if (field != null) {
                                Pair<Long, FacilioField> pair = Pair.of(reading.getParentId(), field);
                                rdmPairs.add(pair);
                                if (rdmPairs.size() == BATCH_SIZE) {
                                    readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
                                    rdmPairs.clear();
                                }
                                readingFieldId = field.getFieldId();
                            }
                        }
                    }
                }
            }

            if (!rdmPairs.isEmpty()) {
                readingDataMeta = fetchRDM(readingDataMeta, rdmPairs);
            }
            if ((readingDataMeta == null) || (readingDataMeta.isEmpty())) {
                LOGGER.info(" reading data meta empty-> " + ", parentId - " + parentId + ", readingFieldId - " + readingFieldId + "  rdm pairs " + rdmPairs + " reading map : " + readingMap);
            }
            context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, readingDataMeta);
        }
        LOGGER.debug("GetReadingDataMetaCommand time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }


    private Map<String, ReadingDataMeta> fetchRDM(Map<String, ReadingDataMeta> readingDataMeta, List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
        Map<String, ReadingDataMeta> rdm = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
        if (readingDataMeta == null) {
            readingDataMeta = rdm;
        } else {
            readingDataMeta.putAll(rdm);
        }
        return readingDataMeta;
    }

}
