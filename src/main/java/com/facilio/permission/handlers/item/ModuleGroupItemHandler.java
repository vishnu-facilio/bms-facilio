package com.facilio.permission.handlers.item;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3ModuleAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ModuleGroupItemHandler implements GroupItemHandler {
    @Override
    public List<PermissionSetGroupingItemContext> getGroupItems() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> allModules = V3ModuleAPI.getSystemModuleWithFeatureLicenceCheck();
        allModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        List<PermissionSetGroupingItemContext> items = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(allModules)) {
            for(FacilioModule module : allModules) {
                if(module != null && module.getModuleId() > 0) {
                    items.add(new PermissionSetGroupingItemContext(module.getModuleId(), module.getDisplayName()));
                }
            }
        }
        return items;
    }
}