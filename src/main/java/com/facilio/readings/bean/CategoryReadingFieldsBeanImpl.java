package com.facilio.readings.bean;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.readings.context.AssetCategoryReadingFieldContext;
import com.facilio.readings.helper.ReadingFieldsUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryReadingFieldsBeanImpl implements CategoryReadingFieldsBean {
    @Override
    public V3AssetCategoryContext getAssetCategoryForModule(@NonNull Long moduleId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_MODULEID","assetModuleId",String.valueOf(moduleId), NumberOperators.EQUALS));
        List<V3AssetCategoryContext> assetCategories = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.ASSET_CATEGORY,null, V3AssetCategoryContext.class,criteria,null);
        if(CollectionUtils.isNotEmpty(assetCategories)) {
            return assetCategories.get(0);
        }
        return null;
    }

    @Override
    public V3UtilityTypeContext getUtilityTypeForModule(@NonNull Long moduleId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("METER_MODULEID","meterModuleId",String.valueOf(moduleId), NumberOperators.EQUALS));
        List<V3UtilityTypeContext> meterTypes = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.Meter.UTILITY_TYPE,null, V3UtilityTypeContext.class,criteria,null);
        if(CollectionUtils.isNotEmpty(meterTypes)) {
            return meterTypes.get(0);
        }
        return null;
    }
    @Override
    public AssetCategoryReadingFieldContext getCategoryReadingField(@NonNull Long moduleId, @NonNull Long fieldId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY_MODULE_ID","categoryModuleId",String.valueOf(moduleId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID","fieldId",String.valueOf(fieldId), NumberOperators.EQUALS));
        List<AssetCategoryReadingFieldContext> list = ReadingFieldsUtil.getCategoryReadingFields(criteria);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    @Override
    public void updateAssetCategoryStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        V3AssetCategoryContext assetCategory = new V3AssetCategoryContext();
        assetCategory.setHasReading(true);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_MODULEID","assetModuleId", StringUtils.join(moduleId,","), NumberOperators.EQUALS));
        FacilioField hasReading = modBean.getField("hasReading", FacilioConstants.ContextNames.ASSET_CATEGORY);
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<>()
                .fields(Collections.singletonList(hasReading))
                .module(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY))
                .andCriteria(criteria);
        updateRecordBuilder.update(assetCategory);
    }

    @Override
    public void updateUtilityTypeStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        V3UtilityTypeContext utilityTypeContext = new V3UtilityTypeContext();
        utilityTypeContext.setHasReading(true);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("METER_MODULEID","meterModuleId", StringUtils.join(moduleId,","), NumberOperators.EQUALS));
        FacilioField hasReading = modBean.getField("hasReading", FacilioConstants.Meter.UTILITY_TYPE);
        UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<>()
                .fields(Collections.singletonList(hasReading))
                .module(modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE))
                .andCriteria(criteria);
        updateRecordBuilder.update(utilityTypeContext);
    }
}