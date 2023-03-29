package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class DefaultBulkInit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (MapUtils.isNotEmpty(ImportConstants.getInsertRecordMap(context))) {
            return false;
        }

        Collection<Pair<Long, Map<String, Object>>> insertRecords = ImportConstants.getInsertRecords(context);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        String moduleName = Constants.getModuleName(context);

        List<Pair<Long, ModuleBaseWithCustomFields>> records = new ArrayList<>();

        for (Pair<Long, Map<String, Object>> rawObj : insertRecords) {
            Long logId = rawObj.getLeft();
            Map<String, Object> rawRecord = rawObj.getRight();
            ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(rawRecord, beanClass);
            Pair logIdVsReordPair = new MutablePair(logId, record);

            records.add(logIdVsReordPair);
        }

        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);

        ImportConstants.setInsertRecordMap(context, recordMap);
        return false;
    }
}
