package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class V3SetAssetCategoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3AssetContext> list = recordMap.get(moduleName);

        for (V3AssetContext asset : list) {

            if (asset != null) {
                if (asset._getSiteId() != null && asset._getSiteId() > 0) {
                    V3SiteContext site = new V3SiteContext();
                    site.setId(asset._getSiteId());
                    asset.setIdentifiedLocation(site);
                }
                if (asset.getSpace() != null) {
                    asset.setCurrentSpaceId(asset.getSpace().getId());
                }
            }
            if (moduleName != null && !moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                V3AssetCategoryContext categoryFromModule = AssetsAPI.getCategoryByAssetModuleV3(module.getModuleId());
                if (categoryFromModule != null) {
                    V3AssetCategoryContext assetCategory = new V3AssetCategoryContext();
                    Long categoryId = categoryFromModule.getId();
                    assetCategory.setId(categoryId);
                    asset.setCategory(assetCategory);
                    context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
                }
            }
        }
        return false;
    }
}
