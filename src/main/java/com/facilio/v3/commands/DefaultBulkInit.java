package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class DefaultBulkInit extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (MapUtils.isNotEmpty(Constants.getRecordMap(context))) {
            return false;
        }

        Collection<JSONObject> bulkRawInput = Constants.getBulkRawInput(context);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = new ArrayList<>();

        for (JSONObject rawObj: bulkRawInput) {
            records.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(rawObj, beanClass));
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);

        Constants.setRecordMap(context, recordMap);
        return false;
    }
}
