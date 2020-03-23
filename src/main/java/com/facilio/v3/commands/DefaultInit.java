package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultInit extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject data = (JSONObject) context.get(Constants.RAW_INPUT);
        ModuleBaseWithCustomFields asBeanFromJson = FieldUtil.getAsBeanFromJson(data, ModuleBaseWithCustomFields.class);
        Map<String, List> recordMap = new HashMap<>();
        recordMap.put((String) context.get(Constants.MODULE_NAME), Arrays.asList(asBeanFromJson));
        context.put(Constants.RECORD_MAP, recordMap);
        return false;
    }
}
