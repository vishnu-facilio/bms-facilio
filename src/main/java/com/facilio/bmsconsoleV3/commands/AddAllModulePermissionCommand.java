package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.ModulePermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAllModulePermissionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioModule.ModuleType> moduleTypeList = Arrays.asList
                (
                    FacilioModule.ModuleType.BASE_ENTITY,
                    FacilioModule.ModuleType.Q_AND_A,
                    FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                    FacilioModule.ModuleType.SUB_ENTITY
                );
        List<FacilioModule> requiredModuleList = new ArrayList<>();
        for(FacilioModule.ModuleType moduleType : moduleTypeList){
            List<FacilioModule> moduleList = modBean.getModuleList(moduleType,false);
            if(CollectionUtils.isNotEmpty(moduleList)){
                requiredModuleList.addAll(moduleList);
            }
        }
        if(CollectionUtils.isNotEmpty(requiredModuleList)) {
            for (FacilioModule module : requiredModuleList) {
                if (!ModulePermissionUtil.generatePermissionMapGroup().containsKey(module.getName())) {
                    if(CollectionUtils.isEmpty(ModulePermissionUtil.getModulePermissions(module.getModuleId()))) {
                        ModulePermissionUtil.addPermissionForModule(module);
                    }
                }
            }
        }
        return false;
    }
}
