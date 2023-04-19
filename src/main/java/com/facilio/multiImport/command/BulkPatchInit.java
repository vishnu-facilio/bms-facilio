package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class BulkPatchInit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(MapUtils.isNotEmpty(ImportConstants.getUpdateRecordMap(context))) {
            return false;
        }
        Collection<Pair<Long, Map<String, Object>>> updateRecords = ImportConstants.getUpdateRecords(context);
        Map<Long,V3Context> logIdVsOldRecord = ImportConstants.getOldRecordsMap(context);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        String moduleName = Constants.getModuleName(context);

        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> updateRecordMap = new HashMap<>();
        List<Pair<Long, ModuleBaseWithCustomFields>> updateRecordsPair = new ArrayList<>();

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        List<ModuleBaseWithCustomFields> records = new LinkedList<>();

        if(CollectionUtils.isEmpty(updateRecords)){
            updateRecordMap.put(moduleName,updateRecordsPair);
            recordMap.put(moduleName,records);
            ImportConstants.setUpdateRecordMap(context, updateRecordMap);
            Constants.setRecordMap(context,recordMap);
            return false;
        }

        for (Pair<Long, Map<String, Object>> rawObj : updateRecords) {

            if(rawObj == null){
                continue;
            }

            Long logId = rawObj.getLeft();
            Map<String, Object> rawRecord = rawObj.getRight();
            V3Context oldRecord = logIdVsOldRecord.get(logId);
            Map<String, Object> oldRecordJson = FieldUtil.getAsJSON(oldRecord);

            Set<String> keys = rawRecord.keySet();
            for (String key : keys) {
                oldRecordJson.put(key, rawRecord.get(key));
            }

            ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(oldRecordJson, beanClass);
            Pair logIdVsReordPair = new MutablePair(logId, record);

            updateRecordsPair.add(logIdVsReordPair);
            records.add(record);

        }

        updateRecordMap.put(moduleName,updateRecordsPair);
        recordMap.put(moduleName,records);

        ImportConstants.setUpdateRecordMap(context, updateRecordMap);
        Constants.setRecordMap(context,recordMap);
        return false;
    }
}
