package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<? extends ModuleBaseWithCustomFields>> recordMap
                = (Map<String, List<? extends ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);

        String moduleName = Constants.getModuleName(context);
        JSONObject recordJSON = FieldUtil.getAsJSON(recordMap);
        ArrayList recordProps = (ArrayList) recordJSON.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);

        List<FacilioField> lookupFields = allFields.stream().filter(i -> i.getDataTypeEnum() == FieldType.LOOKUP).collect(Collectors.toList());

        Map<String, Map<String, Object>> supplements = new HashMap<>();
        supplements.put(moduleName, new HashMap<>());
        for (FacilioField field: lookupFields) {
            supplements.get(moduleName).put(field.getName(), new HashMap<>());
            for (Object propObject: recordProps) {
                Map<String, Object> prop = (Map) propObject;
                Map<String, Object> lookupProp = (Map<String, Object>) prop.get(field.getName());
                if (MapUtils.isEmpty(lookupProp)) {
                    continue;
                }

                // checking for 2 here since most lookups have id and moduleid
                if (lookupProp.size() >= 2 && lookupProp.get("id") != null) {
                    Map suppEntry = (Map) supplements.get(moduleName)
                            .get(field.getName());
                    suppEntry.put(lookupProp.get("id"), lookupProp);

                    JSONObject newLookUpEntry = new JSONObject();
                    newLookUpEntry.put("id", lookupProp.get("id"));
                    prop.put(field.getName(), newLookUpEntry);
                }
            }

            if (MapUtils.isEmpty((Map) supplements.get(moduleName).get(field.getName()))) {
                supplements.get(moduleName).remove(field.getName());
            }
        }
        if (MapUtils.isNotEmpty(supplements.get(moduleName))) {
            Constants.setSupplementMap(context, supplements);
        }
        Constants.setJsonRecordMap(context, recordJSON);
        return false;
    }

}
