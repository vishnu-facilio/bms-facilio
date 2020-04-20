package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssetDepreciationAPI {

    public static AssetDepreciationContext getAssetDepreciation(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);

        SelectRecordsBuilder<AssetDepreciationContext> builder = new SelectRecordsBuilder<AssetDepreciationContext>()
                .module(module)
                .beanClass(AssetDepreciationContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPRECIATION))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        AssetDepreciationContext assetDepreciationContext = builder.fetchFirst();

        if (assetDepreciationContext != null) {
            assetDepreciationContext.setAssetDepreciationRelList(getRelList(assetDepreciationContext.getId()));
        }
        return assetDepreciationContext;
    }

    public static List<AssetDepreciationRelContext> getRelList(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .select(FieldFactory.getAssetDepreciationRelFields())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(id), NumberOperators.EQUALS));
        return FieldUtil.getAsBeanListFromMapList(builder.get(), AssetDepreciationRelContext.class);
    }

    public static void addRelList(long id, List<Long> assetIds) throws Exception {
        if (id > 0 && CollectionUtils.isNotEmpty(assetIds)) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                    .fields(FieldFactory.getAssetDepreciationRelFields());

            List<AssetDepreciationRelContext> list = new ArrayList<>();
            for (Long assetId : assetIds) {
                AssetDepreciationRelContext relContext = new AssetDepreciationRelContext();
                relContext.setAssetId(assetId);
                relContext.setDepreciationId(id);
                list.add(relContext);
            }
            builder.addRecords(FieldUtil.getAsMapList(list, AssetDepreciationRelContext.class));
            builder.save();
        }

    }

    public static void deleteRelList(long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(id), NumberOperators.EQUALS));
        builder.delete();
    }

    public static void addAsset(Long depreciationId, List<Long> assetIds) throws Exception {
        if (CollectionUtils.isEmpty(assetIds)) {
            return;
        }
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getAssetDepreciationRelFields())
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName());
        for (Long assetId : assetIds) {
            Map<String, Object> map = new HashMap<>();
            map.put("assetId", assetId);
            map.put("depreciationId", depreciationId);
            map.put("activated", false);
            builder.addRecord(map);
        }
        builder.save();
    }

    public static Map<Long, List<AssetDepreciationRelContext>> getRelMap(List<Long> depreciationIds) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .select(FieldFactory.getAssetDepreciationRelFields())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", StringUtils.join(depreciationIds, ","), NumberOperators.EQUALS));
        List<AssetDepreciationRelContext> relList = FieldUtil.getAsBeanListFromMapList(builder.get(), AssetDepreciationRelContext.class);
        Map<Long, List<AssetDepreciationRelContext>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(relList)) {
            for (AssetDepreciationRelContext relContext : relList) {
                List<AssetDepreciationRelContext> assetDepreciationRelContexts = map.get(relContext.getDepreciationId());
                if (assetDepreciationRelContexts == null) {
                    assetDepreciationRelContexts = new ArrayList<>();
                    map.put(relContext.getDepreciationId(), assetDepreciationRelContexts);
                }
                assetDepreciationRelContexts.add(relContext);
            }
        }
        return map;
    }

    public static List<AssetDepreciationContext> getAllActiveAssetDepreciation() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);

        SelectRecordsBuilder<AssetDepreciationContext> builder = new SelectRecordsBuilder<AssetDepreciationContext>()
                .module(module)
                .beanClass(AssetDepreciationContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPRECIATION))
                .andCondition(CriteriaAPI.getCondition("ACTIVE", "active", String.valueOf(true), BooleanOperators.IS));
        List<AssetDepreciationContext> assetDepreciationContexts = builder.get();
        if (CollectionUtils.isNotEmpty(assetDepreciationContexts)) {
            List<Long> collect = assetDepreciationContexts.stream().map(AssetDepreciationContext::getId).collect(Collectors.toList());
            Map<Long, List<AssetDepreciationRelContext>> relMap = getRelMap(collect);
            if (MapUtils.isNotEmpty(relMap)) {
                for (AssetDepreciationContext assetDepreciationContext : assetDepreciationContexts) {
                    assetDepreciationContext.setAssetDepreciationRelList(relMap.get(assetDepreciationContext.getId()));
                }
            }
        }
        return assetDepreciationContexts;
    }

    public static List<AssetDepreciationCalculationContext> getAssetDepreciationCalculation(List<Long> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION_CALCULATION);

        SelectRecordsBuilder<AssetDepreciationCalculationContext> builder = new SelectRecordsBuilder<AssetDepreciationCalculationContext>()
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .beanClass(AssetDepreciationCalculationContext.class)
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        List<AssetDepreciationCalculationContext> assetDepreciationCalculationContexts = builder.get();
        return assetDepreciationCalculationContexts;
    }

    public static void insertDepreciationCalculations(List<AssetDepreciationCalculationContext> newList) throws Exception {
        if (CollectionUtils.isNotEmpty(newList)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION_CALCULATION);

            InsertRecordBuilder<AssetDepreciationCalculationContext> builder = new InsertRecordBuilder<AssetDepreciationCalculationContext>()
                    .module(module)
                    .fields(modBean.getAllFields(module.getName()));
            builder.addRecords(newList);
            builder.save();
        }
    }
}
