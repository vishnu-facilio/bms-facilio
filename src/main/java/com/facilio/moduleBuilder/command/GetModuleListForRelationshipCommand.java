package com.facilio.moduleBuilder.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class GetModuleListForRelationshipCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ModuleListConfig.MODULES);
        Map<String, Object> resultMap = (Map<String, Object>) context.getOrDefault(FacilioConstants.ContextNames.RESULT, new HashMap<>());

        List<String> parentModulesList = new ArrayList<String>() {{
            add(FacilioConstants.ContextNames.ASSET);
            add(FacilioConstants.ContextNames.METER_MOD_NAME);
        }};
        Map<String, Object> parentChildModuleMap = new HashMap<>();

        ModuleBean modBean = Constants.getModBean();
        for (String parentModule : parentModulesList) {
            List<FacilioModule> childModules = modBean.getChildModules(modBean.getModule(parentModule), null, null, false);
            List<Map<String, Object>> childModulesListMap = new ArrayList<>();
            for (FacilioModule childModule : childModules) {
                childModulesListMap.add(ResponseFormatUtil.formatModuleBasedOnResponseFields(childModule, (List<String>) context.get("responseFields"), false));
            }
            if (CollectionUtils.isNotEmpty(childModulesListMap)) parentChildModuleMap.put(parentModule, childModulesListMap);
        }

        resultMap.put("subModules", parentChildModuleMap);
        context.put(FacilioConstants.ContextNames.RESULT, resultMap);

        return false;
    }
}
