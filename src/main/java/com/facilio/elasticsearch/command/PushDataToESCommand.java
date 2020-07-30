package com.facilio.elasticsearch.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.elasticsearch.util.ESUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PushDataToESCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
        List<? extends ModuleBaseWithCustomFields> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsToSearchMap = FieldFactory.getAsMap(allFields.stream().filter(field -> {
                if (field.getDataTypeEnum() == FieldType.STRING || field.getDataTypeEnum() == FieldType.NUMBER) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList()));

            List<FacilioField> restrictedFields = allFields.stream().filter(field -> !fieldsToSearchMap.containsKey(field.getName())).collect(Collectors.toList());
            List<JSONObject> objectsToBeAdded = new ArrayList<>();
            for (ModuleBaseWithCustomFields record : list) {
                JSONObject map = FieldUtil.getAsJSON(record);
                for (FacilioField field : restrictedFields) {
                    map.remove(field.getName());
                }
                objectsToBeAdded.add(map);
            }

            ESUtil.addData(module, objectsToBeAdded);
        }
        return false;
    }
}
