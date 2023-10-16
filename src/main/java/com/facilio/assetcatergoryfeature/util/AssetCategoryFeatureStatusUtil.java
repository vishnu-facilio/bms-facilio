package com.facilio.assetcatergoryfeature.util;

import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.assetcatergoryfeature.AssetCategoryFeatureActivationContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.context.NSType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AssetCategoryFeatureStatusUtil {
    public static AssetCategoryFeatureActivationContext fetchStatusFromCategory(Long categoryId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getAssetCategoryFeatureActivationFields())
                .table(ModuleFactory.getAssetCategoryFeatureActivationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", String.valueOf(categoryId), NumberOperators.EQUALS));

        List<Map<String, Object>> resultProp = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(resultProp)) {
            AssetCategoryFeatureActivationContext assetCategoryFeatureActivation = FieldUtil.getAsBeanFromMap(resultProp.get(0), AssetCategoryFeatureActivationContext.class);
            return assetCategoryFeatureActivation;
        }
        return new AssetCategoryFeatureActivationContext();
    }

    public static void updateCategoryLevelExecStatus(AssetCategoryFeatureActivationContext categoryContext, Long categoryId) throws Exception {
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getAssetCategoryFeatureActivationModule().getTableName())
                .fields(FieldFactory.getAssetCategoryFeatureActivationFields())
                .andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", String.valueOf(categoryId), NumberOperators.EQUALS));
        updateRecordBuilder.update(FieldUtil.getAsProperties(categoryContext));
    }


    public static Boolean validateFldWithNs(Long categoryId, NSType type) throws Exception {
        AssetCategoryFeatureActivationContext assetCategoryFeature = fetchStatusFromCategory(categoryId);
        if (assetCategoryFeature != null) {
            switch (type) {
                case READING_RULE:
                    return assetCategoryFeature.getReadingRulesStatus();
                case SENSOR_RULE:
                    return assetCategoryFeature.getSensorRulesStatus();
            }
        }
        return Boolean.TRUE;
    }

}
