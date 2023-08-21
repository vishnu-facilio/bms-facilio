package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluatedSystemButtonsForEachRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        List<Map<String,Object>> systemButtonsForRecords = new ArrayList<>();

        for (ModuleBaseWithCustomFields record : records){
            Map<String,Object> evaluatedButtonIdsWithRecordIds = new HashMap<>();
            evaluatedButtonIdsWithRecordIds.put("id",record.getId());
            evaluatedButtonIdsWithRecordIds.put("evaluatedButtonIds",record.getEvaluatedButtonIds());
            systemButtonsForRecords.add(evaluatedButtonIdsWithRecordIds);
        }

        context.put(Constants.SYSTEM_BUTTONS_RECORDS,systemButtonsForRecords);
        return false;
    }
}
