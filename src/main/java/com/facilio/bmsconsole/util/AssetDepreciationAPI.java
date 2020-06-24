package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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

    public static void removeAsset(long depreciationId, List<Long> assetIds) throws Exception {
        if (CollectionUtils.isEmpty(assetIds)) {
            return;
        }
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(depreciationId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", StringUtils.join(assetIds, ","), NumberOperators.EQUALS));
        builder.delete();
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

    public static void saveDepreciationCalculationList(List<AssetDepreciationCalculationContext> newList,
                                                       AssetDepreciationContext assetDepreciationContext) throws Exception {
        // save list and update asset current price
        if (CollectionUtils.isNotEmpty(newList)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
            FacilioField field = modBean.getField(assetDepreciationContext.getCurrentPriceFieldId(), assetModule.getName());

            AssetDepreciationAPI.insertDepreciationCalculations(newList);

            Map<AssetContext, List<AssetDepreciationCalculationContext>> assetVsCalculationList = new HashMap<>();
            for (AssetDepreciationCalculationContext calculationContext : newList) {
                List<AssetDepreciationCalculationContext> list = assetVsCalculationList.get(calculationContext.getAsset());
                if (list == null) {
                    list = new ArrayList<>();
                    assetVsCalculationList.put(calculationContext.getAsset(), list);
                }
                list.add(calculationContext);
            }

            for (AssetContext asset : assetVsCalculationList.keySet()) {
                List<AssetDepreciationCalculationContext> calculationContexts = assetVsCalculationList.get(asset);

                // take only latest context
                AssetDepreciationCalculationContext context = calculationContexts.get(calculationContexts.size() - 1);

                asset.setCurrentPrice(((Float) context.getCurrentPrice()).intValue());
                UpdateRecordBuilder<AssetContext> updateRecordBuilder = new UpdateRecordBuilder<AssetContext>()
                        .module(assetModule)
                        .fields(Collections.singletonList(field))
                        .andCondition(CriteriaAPI.getIdCondition(asset.getId(), assetModule));
                updateRecordBuilder.update(asset);

                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                        .fields(Collections.singletonList(FieldFactory.getField("lastCalculatedId", "LAST_CALCULATED_ID", ModuleFactory.getAssetDepreciationRelModule(), FieldType.NUMBER)))
                        .andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(asset.getId()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(context.getDepreciationId()), NumberOperators.EQUALS));
                Map<String, Object> map = new HashMap<>();
                map.put("lastCalculatedId", context.getId());
                builder.update(map);
            }
        }
    }

    public static void deleteAssetDepreciationCalculation(AssetDepreciationContext assetDepreciation, AssetContext assetContext) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION_CALCULATION);

        DeleteRecordBuilder<AssetDepreciationCalculationContext> builder = new DeleteRecordBuilder<AssetDepreciationCalculationContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(assetDepreciation.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(assetContext.getId()), NumberOperators.EQUALS));
        builder.delete();

        assetContext.setCurrentPrice(-99);
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        FacilioField currentPrice = modBean.getField("currentPrice", assetModule.getName());
        UpdateRecordBuilder<AssetContext> assetBuilder = new UpdateRecordBuilder<AssetContext>()
                .module(assetModule)
                .fields(Collections.singletonList(currentPrice))
                .andCondition(CriteriaAPI.getIdCondition(assetContext.getId(), assetModule));
        assetBuilder.update(assetContext);
    }

    public static List<AssetDepreciationCalculationContext> calculateAssetDepreciation(AssetDepreciationContext assetDepreciation, AssetContext assetContext,
                                                                                       AssetDepreciationCalculationContext lastCalculation) throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);

            float totalPrice = ((Number) FieldUtil.getValue(assetContext, assetDepreciation.getTotalPriceFieldId(), assetModule)).floatValue();
            if (totalPrice == -1) {
                throw new IllegalArgumentException("Price cannot be empty");
            }
            float salvageAmount = ((Number) FieldUtil.getValue(assetContext, assetDepreciation.getSalvagePriceFieldId(), assetModule)).floatValue();
            long date = (long) FieldUtil.getValue(assetContext, assetDepreciation.getStartDateFieldId(), assetModule);
            float currentAmount = ((Number) FieldUtil.getValue(assetContext, assetDepreciation.getCurrentPriceFieldId(), assetModule)).floatValue();
            if (currentAmount == -1) {
                currentAmount = totalPrice;
            }

            List<AssetDepreciationCalculationContext> list = new ArrayList<>();
            if (lastCalculation != null) {
                date = lastCalculation.getCalculatedDate();
            }
            else {
                if (date == -1) {
                    throw new IllegalArgumentException("Start date cannot be empty");
                }

                // add initial depreciation
                AssetDepreciationCalculationContext newlyCalculated = new AssetDepreciationCalculationContext();
                newlyCalculated.setAsset(assetContext);
                newlyCalculated.setCalculatedDate(DateTimeUtil.getMonthStartTimeOf(date));
                newlyCalculated.setCurrentPrice(currentAmount);
                newlyCalculated.setDepreciatedAmount(0);
                newlyCalculated.setDepreciationId(assetDepreciation.getId());
                list.add(newlyCalculated);
            }

            while (true) {

                long nextDate = assetDepreciation.nextDate(date);
                if (nextDate > DateTimeUtil.getDayStartTime()) {
                    // Still time hasn't arrived to calculate
                    break;
                }


                float totalDepreciationAmount = totalPrice;
                if (salvageAmount > 0) {
                    totalDepreciationAmount -= salvageAmount;
                }

                float newPrice = ((Number) assetDepreciation.getDepreciationTypeEnum().nextDepreciatedUnitPrice(totalDepreciationAmount, assetDepreciation.getFrequency(), currentAmount)).floatValue();

                AssetDepreciationCalculationContext newlyCalculated = new AssetDepreciationCalculationContext();
                newlyCalculated.setAsset(assetContext);
                newlyCalculated.setCalculatedDate(DateTimeUtil.getMonthStartTimeOf(nextDate));
                newlyCalculated.setCurrentPrice(newPrice);
                newlyCalculated.setDepreciatedAmount(currentAmount - newPrice);
                newlyCalculated.setDepreciationId(assetDepreciation.getId());
                list.add(newlyCalculated);

                date = nextDate;
                currentAmount = newPrice;
            }
            return list;
        } catch (IllegalArgumentException ex) {
            // ignore for those asset that has invalid data
        }
        return null;
    }

    public static AssetDepreciationContext getDepreciationOfAsset(Long assetId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .select(FieldFactory.getAssetDepreciationRelFields())
                .andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(assetId), NumberOperators.EQUALS));
        AssetDepreciationRelContext relContext = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), AssetDepreciationRelContext.class);
        if (relContext != null) {
            return getAssetDepreciation(relContext.getDepreciationId());
        }
        return null;
    }
}
