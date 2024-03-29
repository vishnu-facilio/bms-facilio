package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.remotemonitoring.compute.FilterAlarmUtil;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAssetRelationsForAssetCategoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long assetCategoryId = (long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY_ID);

        V3AssetCategoryContext assetCategory = (V3AssetCategoryContext) V3Util.getRecord(ModuleFactory.getAssetCategoryModule().getName(), assetCategoryId, null);
        long assetCategoryModuleId = assetCategory.getAssetModuleID();
        List<RelationContext> relationsForAssetCategory = RemoteMonitorUtils.getAssetRelationForAssetCategory(assetCategoryModuleId);
        RelationUtil.fillRelation(relationsForAssetCategory);
        List<RelationContext> relationContextList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(relationsForAssetCategory)) {
            for(RelationContext relationContext : relationsForAssetCategory) {
                RelationContext relationContextNew = FilterAlarmUtil.updateRelationMappingOrder(relationContext, assetCategoryModuleId, true);
                relationContextList.add(relationContextNew);
            }
        }
        context.put(FacilioConstants.ContextNames.RELATIONS_FOR_ASSET_CATEGORIES, relationContextList);

        return false;
    }
}
