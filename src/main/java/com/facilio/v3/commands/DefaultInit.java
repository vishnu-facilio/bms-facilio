package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        Class classFromModule = FacilioConstants.ContextNames.getClassFromModule(module);
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromJson(data, classFromModule);

        Long id = (Long) context.get(Constants.RECORD_ID);

        if (id != null) {
            moduleRecord.setId(id);
        }

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(moduleName, Arrays.asList(moduleRecord));

        context.put(Constants.RECORD_MAP, recordMap);
        return false;
    }
}
