package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PivotThemeCustomizationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> pivotTable = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA);
        Map<String, String> headers = (Map<String, String>) pivotTable.get("headers");
        ArrayList<Map<String,Object>> records = (ArrayList<Map<String,Object>>) pivotTable.get("records");
        JSONObject templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        if(!templateJson.containsKey("theme") || templateJson.get("theme") == null)
        {
            return false;
        }
        Map<String,Object> theme = (Map<String,Object>) templateJson.get("theme");
        if(!theme.containsKey("number") || theme.get("number") == null)
        {
            return false;
        }
        boolean rowNumber = (boolean) theme.get("number");
        String rowNumberKey = "number";
        if(rowNumber){
            headers.put(rowNumberKey, "#");
            int count = 1;
            for(Map<String,Object> record: records){
                Map<String,Object> tempMap = new HashMap<>();
                tempMap.put("formattedValue", count);
                record.put(rowNumberKey,tempMap);
                count++;
            }
        }
        return false;
    }
}
