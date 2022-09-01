package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.ModulePermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class AddModulePermissionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Integer> moduleTypeList = Arrays.asList
                (
                        FacilioModule.ModuleType.BASE_ENTITY.getValue(),
                        FacilioModule.ModuleType.Q_AND_A.getValue(),
                        FacilioModule.ModuleType.Q_AND_A_RESPONSE.getValue(),
                        FacilioModule.ModuleType.SUB_ENTITY.getValue()
                );
        Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);
        if(moduleId != null){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleId);
            if(module != null){
                if(moduleTypeList.contains(module.getType())){
                    ModulePermissionUtil.addPermissionForModule(module);
                }
            }
        }
        return false;
    }
}
