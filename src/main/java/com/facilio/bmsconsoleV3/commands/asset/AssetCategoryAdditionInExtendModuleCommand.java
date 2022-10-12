package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

public class AssetCategoryAdditionInExtendModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> assetList = (List<ModuleBaseWithCustomFields>) (recordMap.get("asset"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Set<String> extendedModules = new HashSet<>();
        for (ModuleBaseWithCustomFields asset : assetList) {
            AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(((V3AssetContext) (asset)).getCategory().getId());
            long assetModuleID = assetCategory.getAssetModuleID();
            FacilioModule module = modBean.getModule(assetModuleID);
            if(extendedModules.contains(module.getName())){
                List<ModuleBaseWithCustomFields> list = recordMap.get(module.getName());
                list.add(asset);
                recordMap.put(module.getName(),list);
            }else{
                extendedModules.add(module.getName());
                recordMap.put(module.getName(), new ArrayList(Arrays.asList(asset)));
            }
            Constants.setRecordMap(context, recordMap);
            long categoryId = -1;
            if (assetCategory != null && assetCategory.getId() != 0) {
                categoryId = assetCategory.getId();
            }
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
            context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        }
        Constants.setExtendedModules(context, extendedModules);
        List<V3AssetContext> v3assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.ASSET));
        for(V3AssetContext asset : v3assetList){
            if (asset.getSpace() == null || asset.getSpace().getId() < 0) {
                V3BaseSpaceContext assetLocation = new V3BaseSpaceContext();
                assetLocation.setId(asset.getSiteId());
                asset.setSpace(assetLocation);
            }
        }
        return false;
    }
}
