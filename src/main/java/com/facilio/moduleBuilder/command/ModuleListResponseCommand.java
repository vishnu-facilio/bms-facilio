package com.facilio.moduleBuilder.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleListResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> responseFields = (List<String>) context.get(FacilioConstants.ModuleListConfig.RESPONSE_FIELDS);

        if(CollectionUtils.isNotEmpty(responseFields)) {
            List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ModuleListConfig.MODULES);

            Map<String, Object> resultMap = new HashMap<>();
            List<Map<String, Object>> modulesMapList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(modules)) {
                for(FacilioModule module : modules) {
                    modulesMapList.add(ResponseFormatUtil.formatModuleBasedOnResponseFields(module, responseFields, true));
                }
            }
            resultMap.put(FacilioConstants.ContextNames.MODULE_LIST, modulesMapList);
            context.put(FacilioConstants.ContextNames.RESULT, resultMap);

        } else {
            throw new IllegalArgumentException("define response fieldNames");
        }
        return false;
    }
}
