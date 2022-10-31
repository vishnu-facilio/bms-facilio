package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetCustomModulesListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> customModules = modBean.getModuleList(FacilioModule.ModuleType.valueOf(moduleType), true, pagination, searchString);

        if(CollectionUtils.isNotEmpty(customModules) && (AccountUtil.getCurrentOrg().getOrgId() == 321l || AccountUtil.getCurrentOrg().getOrgId() == 173l)){
            List<FacilioModule> splModules = customModules.stream()
                    .filter(mod->mod.getName().equals("custom_tenantcontract") || mod.getName().equals("custom_timesheetmanagement") || mod.getName().equals("custom_servicebilllineitems"))
                    .collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(splModules)){
                splModules.sort(new Comparator<FacilioModule>() {
                    @Override
                    public int compare(FacilioModule o1, FacilioModule o2) {
                        return Long.compare(o2.getModuleId(),o1.getModuleId());
                    }
                });
                customModules.removeAll(splModules);
                customModules.addAll(0, splModules);
            }
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, customModules);
        return false;
    }
}
