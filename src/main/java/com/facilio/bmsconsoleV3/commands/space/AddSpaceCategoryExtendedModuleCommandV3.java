package com.facilio.bmsconsoleV3.commands.space;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class AddSpaceCategoryExtendedModuleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        Map<Long, SpaceCategoryContext> spaceCategories = new HashMap<>();
        Map<Long, FacilioModule> spaceCategoryModuleIds = new HashMap<>();
            for (ModuleBaseWithCustomFields record : records) {
                V3SpaceContext spaceContext = (V3SpaceContext) record;
                SpaceCategoryContext category = null;
                if(!spaceCategories.containsKey(spaceContext.getSpaceCategoryId())) {
                    category = (SpaceCategoryContext) RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE_CATEGORY, spaceContext.getSpaceCategoryId());
                    if (category != null && !spaceCategories.containsKey(category.getId())) {
                        spaceCategories.put(category.getId(), category);
                    }
                }
                else {
                    category = spaceCategories.get(spaceContext.getSpaceCategoryId());
                }
                if (category.getSpaceModuleId() > 0) {
                    FacilioModule module = null;
                    if(spaceCategoryModuleIds.containsKey(category.getSpaceModuleId())) {
                        module = spaceCategoryModuleIds.get(category.getSpaceModuleId());
                    }
                    else {
                        module = modBean.getModule(category.getSpaceModuleId());
                    }
                    Set<String> extendedModules = new HashSet<>();
                    extendedModules.add(module.getName());
                    recordMap.put(module.getName(), Collections.singletonList(spaceContext));
                    Constants.setRecordMap(context, recordMap);
                    Constants.setExtendedModules(context, extendedModules);
                 }
            }
        return false;
    }
}
