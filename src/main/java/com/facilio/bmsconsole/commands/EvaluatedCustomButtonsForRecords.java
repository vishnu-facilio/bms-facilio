package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EvaluatedCustomButtonsForRecords extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<? extends ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        List<Map<String,Object>> customButtonForRecords = new ArrayList<>();

            for (ModuleBaseWithCustomFields record : records){
                    Map<String,Object> evaluatedButtonIdsWithRecordIds = new HashMap<>();
                    evaluatedButtonIdsWithRecordIds.put("id",record.getId());
                    evaluatedButtonIdsWithRecordIds.put("evaluatedButtonIds",record.getEvaluatedButtonIds());
                    customButtonForRecords.add(evaluatedButtonIdsWithRecordIds);
                }

        context.put(Constants.CUSTOM_BUTTONS_RECORDS,customButtonForRecords);

        return false;
    }
}
