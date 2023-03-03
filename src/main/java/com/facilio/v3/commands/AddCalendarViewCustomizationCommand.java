package com.facilio.v3.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AddCalendarViewCustomizationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject jsonRecordMap = Constants.getJsonRecordMap(context);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        List<CustomizationDataContext> customizationDataContextList = new ArrayList<>();
        if (jsonRecordMap != null & !jsonRecordMap.isEmpty()) {
            List<Map<String, Object>> recordMapList = (List<Map<String, Object>>) jsonRecordMap.get(moduleName);
            for (Map<String, Object> recordMap : recordMapList) {
                CustomizationDataContext customizationDataContext = new CustomizationDataContext();
                customizationDataContext.setData(recordMap);
                customizationDataContextList.add(customizationDataContext);
            }
        }

        context.put(FacilioConstants.ViewConstants.CALENDAR_VIEW_CUSTOMIZATON_DATAMAP, customizationDataContextList);

        return false;
    }
}
