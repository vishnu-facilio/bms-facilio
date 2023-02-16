package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetClassificationModulesCommand extends FacilioCommand {
    private static final List<String> MODULES = Arrays.asList(new String[] {
            FacilioConstants.ContextNames.WORK_ORDER,
            FacilioConstants.ContextNames.ASSET,
            FacilioConstants.ContextNames.SITE,
            FacilioConstants.ContextNames.BUILDING,
            FacilioConstants.ContextNames.FLOOR,
            FacilioConstants.ContextNames.SPACE
    });
    @Override
    public boolean executeCommand(Context context) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> classificationSupportedModules = new ArrayList<>();

        List<FacilioModule> customModules = new ArrayList<>();
        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        classificationSupportedModules.addAll(customModules);
        classificationSupportedModules.addAll(modBean.getModuleList(MODULES));
        context.put(FacilioConstants.ContextNames.MODULE_LIST, classificationSupportedModules);

        return false;
    }
}
