package com.facilio.readings.bean;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.readings.context.AssetCategoryReadingFieldContext;
import com.facilio.remotemonitoring.context.*;
import lombok.NonNull;

import java.util.List;

public interface CategoryReadingFieldsBean {
    V3AssetCategoryContext getAssetCategoryForModule(@NonNull Long moduleId) throws Exception;
    V3UtilityTypeContext getUtilityTypeForModule(@NonNull Long moduleId) throws Exception;
    void updateAssetCategoryStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception;
    void updateUtilityTypeStatus(@NonNull List<Long> moduleId,Boolean status) throws Exception;
    AssetCategoryReadingFieldContext getCategoryReadingField(@NonNull Long moduleId, @NonNull Long fieldId) throws Exception;
}