package com.facilio.moduleBuilder.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveStateFlowDisabledModules extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ModuleListConfig.MODULES);

        if(CollectionUtils.isNotEmpty(modules)) {
            context.put(FacilioConstants.ModuleListConfig.MODULES, modules.stream().filter(FacilioModule::isStateFlowEnabled)
                                                                          .collect(Collectors.toList()));

        }
        return false;
    }
}
