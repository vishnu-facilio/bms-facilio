package com.facilio.readings.bean;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.cache.CacheUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readings.context.AssetCategoryReadingFieldContext;
import com.facilio.remotemonitoring.context.*;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class CategoryReadingFieldsBeanCacheImpl extends CategoryReadingFieldsBeanImpl implements CategoryReadingFieldsBean {

    @Override
    public V3AssetCategoryContext getAssetCategoryForModule(@NonNull Long moduleId) throws Exception {
        FacilioCache<String, V3AssetCategoryContext> assetCategoryCache = LRUCache.getAssetCategoryCache();
        String key = CacheUtil.MODULE_KEY(AccountUtil.getCurrentOrg().getId(), moduleId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(assetCategoryCache, key, () -> {
            return super.getAssetCategoryForModule(moduleId);
        });
    }
    @Override
    public V3UtilityTypeContext getUtilityTypeForModule(@NonNull Long moduleId) throws Exception {
        FacilioCache<String, V3UtilityTypeContext> cache = LRUCache.getMeterTypeCache();
        String key = CacheUtil.MODULE_KEY(AccountUtil.getCurrentOrg().getId(), moduleId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(cache, key, () -> {
            return super.getUtilityTypeForModule(moduleId);
        });
    }


    @Override
    public void updateAssetCategoryStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception {
        super.updateAssetCategoryStatus(moduleId,status);
        FacilioCache<String, V3AssetCategoryContext> cache = LRUCache.getAssetCategoryCache();
        if(CollectionUtils.isNotEmpty(moduleId)) {
            for(Long module : moduleId) {
                String key = CacheUtil.MODULE_KEY(AccountUtil.getCurrentOrg().getId(), module);
                cache.remove(key);
            }
        }
    }

    @Override
    public void updateUtilityTypeStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception {
        super.updateUtilityTypeStatus(moduleId,status);
        FacilioCache<String, V3UtilityTypeContext> cache = LRUCache.getMeterTypeCache();
        if(CollectionUtils.isNotEmpty(moduleId)) {
            for(Long module : moduleId) {
                String key = CacheUtil.MODULE_KEY(AccountUtil.getCurrentOrg().getId(), module);
                cache.remove(key);
            }
        }
    }

    public AssetCategoryReadingFieldContext getCategoryReadingField(@NonNull Long moduleId, @NonNull Long fieldId) throws Exception {
        FacilioCache<String, AssetCategoryReadingFieldContext> cache = LRUCache.getAssetCategoryReadingFieldCache();
        String key = CacheUtil.READING_FIELD_KEY(AccountUtil.getCurrentOrg().getId(), moduleId, fieldId);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(cache, key, () -> {
            return super.getCategoryReadingField(moduleId,fieldId);
        });
    }
}