package com.facilio.analytics.v2.command;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;

public class V2GetFaultReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long categoryId = (Long) context.get("category");
        JSONObject fault_readings = AssetsAPI.getAssetsWithReadingsForSpecificCategory(null, Collections.singletonList(categoryId), true, false);
        if(fault_readings != null && fault_readings.containsKey("fields")) {
            context.put("fields", fault_readings.get("fields"));
        }
        return false;
    }
}
