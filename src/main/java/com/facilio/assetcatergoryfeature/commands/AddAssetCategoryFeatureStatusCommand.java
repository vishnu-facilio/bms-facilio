package com.facilio.assetcatergoryfeature.commands;

import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.assetcatergoryfeature.AssetCategoryFeatureActivationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddAssetCategoryFeatureStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> category = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<V3AssetCategoryContext> assetCategoryList = (List<V3AssetCategoryContext>) category.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
        if (CollectionUtils.isNotEmpty(assetCategoryList)) {
            V3AssetCategoryContext categoryContext = assetCategoryList.get(0);
            addConnectedCategory(categoryContext.getId());
        }
        return false;
    }

    private void addConnectedCategory(Long categoryId) throws Exception {
        AssetCategoryFeatureActivationContext connectedCategory = new AssetCategoryFeatureActivationContext(categoryId);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getAssetCategoryFeatureActivationFields())
                .table(ModuleFactory.getAssetCategoryFeatureActivationModule().getTableName());
        insertRecordBuilder.insert(FieldUtil.getAsProperties(connectedCategory));
    }
}
