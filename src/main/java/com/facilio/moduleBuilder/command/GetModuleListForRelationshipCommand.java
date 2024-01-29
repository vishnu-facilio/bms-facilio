package com.facilio.moduleBuilder.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetModuleListForRelationshipCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ModuleListConfig.MODULES);

        List<String> parentModulesList = new ArrayList<String>() {{
            add(FacilioConstants.ContextNames.ASSET);
            add(FacilioConstants.ContextNames.METER_MOD_NAME);
        }};

        if (CollectionUtils.isNotEmpty(modules)) {
            ModuleBean modBean = Constants.getModBean();
            for (String parentModule : parentModulesList) {
                List<FacilioModule> extendedModules = modBean.getChildModules(modBean.getModule(parentModule), null, null, false);
                if (CollectionUtils.isNotEmpty(extendedModules)) modules.addAll(extendedModules);
            }
        }

        context.put(FacilioConstants.ModuleListConfig.MODULES, modules);

        return false;
    }
}
