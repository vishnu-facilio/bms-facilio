package com.facilio.readings.helper;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.cache.CacheUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readings.bean.CategoryReadingFieldsBean;
import com.facilio.readings.context.AssetCategoryReadingFieldContext;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ReadingFieldsUtil {
    public static void rollupReadingFieldsStatus(Collection<ReadingDataMeta> rdm) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        CategoryReadingFieldsBean readingFieldsBean = (CategoryReadingFieldsBean) BeanFactory.lookup("CategoryReadingFieldsBean");
        List<AssetCategoryReadingFieldContext> dataList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rdm)) {
            for (ReadingDataMeta readingDataMeta : rdm) {
                if (readingDataMeta != null) {
                    AssetCategoryReadingFieldContext data = new AssetCategoryReadingFieldContext();
                    data.setFieldId(readingDataMeta.getFieldId());
                    data.setStatus(true);
                    FacilioField readingField = modBean.getField(readingDataMeta.getFieldId());
                    FacilioModule categoryModule = modBean.getParentModule(readingField.getModuleId());
                    AssetCategoryReadingFieldContext existingMapping = readingFieldsBean.getCategoryReadingField(categoryModule.getModuleId(), readingDataMeta.getFieldId());
                    data.setCategoryModuleId(categoryModule.getModuleId());
                    if (existingMapping == null || !existingMapping.isActive()) {
                        dataList.add(data);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            addCategoryReadingFields(dataList);
            updateCategoryReadingStatus(dataList);
        }
    }

    public static void addCategoryReadingFields(List<AssetCategoryReadingFieldContext> list) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
        if (CollectionUtils.isNotEmpty(list)) {
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getAssetCategoryReadingFieldsFields())
                    .table(ModuleFactory.getAssetCategoryReadingFieldsModule().getTableName());
            List<Map<String, Object>> props = FieldUtil.getAsMapList(list, AssetCategoryReadingFieldContext.class);
            insertRecordBuilder.addRecords(props);
            insertRecordBuilder.save();
        }
    }

    public static void updateCategoryReadingStatus(List<AssetCategoryReadingFieldContext> list) throws Exception {
        CategoryReadingFieldsBean readingFieldsBean = (CategoryReadingFieldsBean) BeanFactory.lookup("CategoryReadingFieldsBean");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(list)) {
            for (AssetCategoryReadingFieldContext data : list) {
                FacilioModule module = modBean.getModule(data.getCategoryModuleId());
                if (module != null && module.getExtendModule() != null) {
                    FacilioModule resModule = modBean.getModule(module.getExtendModule().getModuleId());
                    if (resModule != null) {
                        if (resModule.getName().equals(FacilioConstants.ContextNames.ASSET)) {
                            V3AssetCategoryContext assetCategoryContext = readingFieldsBean.getAssetCategoryForModule(data.getCategoryModuleId());
                            if (assetCategoryContext != null && assetCategoryContext.getHasReading() == null) {
                                readingFieldsBean.updateAssetCategoryStatus(Collections.singletonList(data.getCategoryModuleId()), true);
                            }
                        } else if (resModule.getName().equals(FacilioConstants.Meter.METER)) {
                            V3UtilityTypeContext meterType = readingFieldsBean.getUtilityTypeForModule(data.getCategoryModuleId());
                            if (meterType != null && meterType.getHasReading() == null) {
                                readingFieldsBean.updateUtilityTypeStatus(Collections.singletonList(data.getCategoryModuleId()), true);
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<AssetCategoryReadingFieldContext> getCategoryReadingFields(Criteria extraCriteria) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getAssetCategoryReadingFieldsFields())
                .table(ModuleFactory.getAssetCategoryReadingFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", Boolean.TRUE.toString(), BooleanOperators.IS));
        if (extraCriteria != null) {
            selectRecordBuilder.andCriteria(extraCriteria);
        }
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<AssetCategoryReadingFieldContext> list = FieldUtil.getAsBeanListFromMapList(props, AssetCategoryReadingFieldContext.class);
            return list;
        }
        return null;
    }
}
