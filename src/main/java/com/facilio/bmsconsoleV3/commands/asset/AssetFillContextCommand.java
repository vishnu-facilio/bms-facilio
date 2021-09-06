package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AssetFillContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        V3AssetContext asset = new V3AssetContext();
        for (ModuleBaseWithCustomFields record: records) {
            asset = (V3AssetContext) record;
        }

        context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
        if (asset.getCategory() != null && asset.getCategory().getId() > 0) {
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, asset.getCategory().getId());
        } else {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Category cannot be empty");
        }
        if (asset.getSpace() == null || asset.getSpace().getId() < 0) {
            V3BaseSpaceContext assetLocation = new V3BaseSpaceContext();
            assetLocation.setId(asset.getSiteId());
            asset.setSpace(assetLocation);
        }

        if(asset._getSiteId() != null && asset._getSiteId() > 0) {
            V3SiteContext site = new V3SiteContext();
            site.setId(asset._getSiteId());
            asset.setIdentifiedLocation(site);
        }
        if(asset.getSpace() != null) {
            asset.setCurrentSpaceId(asset.getSpace().getId());
        }

        return false;
    }
}
