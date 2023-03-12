package com.facilio.bmsconsoleV3.commands.assetCategory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddAssetCategoryModuleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Get the request payload data as Map
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        V3AssetCategoryContext assetCategory = (V3AssetCategoryContext) recordMap.get("assetcategory").get(0);

        // set Name if it's null or empty
        if (assetCategory.getName() == null || assetCategory.getName().isEmpty()) {
            if (assetCategory.getDisplayName() != null && !assetCategory.getDisplayName().isEmpty()) {
                assetCategory.setName(assetCategory.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""));
            }
        }

        // Get the "asset" Module object
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule("asset");

        String name = assetCategory.getName();
        String displayName = assetCategory.getDisplayName();

        // Create a new module for the new Asset Category
        FacilioModule module = new FacilioModule();
        module.setName("custom_" + name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""));
        module.setDisplayName(displayName != null && !displayName.trim().isEmpty() ? displayName : name);
        module.setTableName("AssetCustomModuleData");
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setExtendModule(assetModule);
        module.setTrashEnabled(true);

        // Put the @module in the scope of Context under the name FacilioConstants.ContextNames.MODULE_LIST
        context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        return false;
    }
}
