package com.facilio.bmsconsoleV3.commands.plannedmaintenance;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.chain.Context;
import org.json.*;

import java.util.*;

public class handlePmV2TriggerBeforeImport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
        List<PMTriggerV2> pmTriggerV2List = new ArrayList<>();
        for(Map<String,Object> row : allRows) {
            String rowContextString = String.valueOf(row.get("rowContextString"));
            JSONArray jsonArr = new JSONArray(rowContextString);
            for(int i=0; i<jsonArr.length(); i++) {
                PMTriggerV2 pmTriggerV2Object = new PMTriggerV2();
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                JSONObject colval = new JSONObject();
                if (jsonObject.length() < 1) {
                    return false;
                } else if (jsonObject.isNull("colVal")) {
                    return false;
                }
                else {
                    colval = jsonObject.getJSONObject("colVal");
                    if (colval.isNull("Trigger")) {
                        return false;
                    } else {
                        Long pmId = colval.getLong("PmId");
                        String trigger = colval.getString("Trigger");
                        JSONObject triggerObj = new JSONObject(trigger);
                        Long startTime = null;
                        Long endTime = null;
                        String pmTriggerFrequency = null;
                        Long formId = null;
                        if (!triggerObj.isNull("startTime")) {
                            startTime = triggerObj.getLong("startTime");
                        }
                        if (!triggerObj.isNull("endTime")) {
                            endTime = triggerObj.getLong("endTime");
                        }
                        if (!triggerObj.isNull("pmTriggerFrequency")) {
                            pmTriggerFrequency = triggerObj.getString("pmTriggerFrequency");
                        }
                        triggerObj.remove("startTime");
                        triggerObj.remove("endTime");
                        triggerObj.remove("pmTriggerFrequency");

                        ModuleBean modbean = Constants.getModBean();
                        FacilioModule pmTriggerV2Module = modbean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);

                        pmTriggerV2Object.setSchedule(String.valueOf(triggerObj));
                        if(endTime != null) {
                            pmTriggerV2Object.setEndTime(endTime);
                        }
                        if(startTime != null) {
                            pmTriggerV2Object.setStartTime(startTime);
                        }
                        else if(startTime == null){
                            pmTriggerV2Object.setStartTime(System.currentTimeMillis());
                        }
                        pmTriggerV2Object.setFrequencyEnum(PMTriggerV2.PMTriggerFrequency.valueOf(pmTriggerFrequency));
                        pmTriggerV2Object.setType(1);
                        pmTriggerV2Object.setPmId(pmId);

                        List<ModuleBaseWithCustomFields> createTriggerRecords = new ArrayList<>();
                        createTriggerRecords.add(pmTriggerV2Object);

                        FacilioContext facilioContext = V3Util.createRecord(pmTriggerV2Module, createTriggerRecords);
                        pmTriggerV2List.add(pmTriggerV2Object);
                    }

                }
            }
        }
        context.put("trigger", pmTriggerV2List);
        return false;
    }
}
