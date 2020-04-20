package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssetDepreciationJob extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        List<AssetDepreciationContext> assetDepreciationList = AssetDepreciationAPI.getAllActiveAssetDepreciation();
        if (CollectionUtils.isNotEmpty(assetDepreciationList)) {
            for (AssetDepreciationContext assetDepreciationContext : assetDepreciationList) {
                List<AssetDepreciationRelContext> assetDepreciationRelList = assetDepreciationContext.getAssetDepreciationRelList();
                if (CollectionUtils.isNotEmpty(assetDepreciationRelList)) {

                    List<Long> assetIds = assetDepreciationRelList.stream().map(AssetDepreciationRelContext::getAssetId).collect(Collectors.toList());
                    List<AssetContext> assetContextList = AssetsAPI.getAssetInfo(assetIds);
                    Map<Long, AssetContext> assetMap = assetContextList.stream().collect(Collectors.toMap(AssetContext::getId, Function.identity()));

                    List<Long> lastCalculatedList = assetDepreciationRelList.stream().filter(rel -> rel.getLastCalculatedId() > 0)
                            .map(AssetDepreciationRelContext::getLastCalculatedId).collect(Collectors.toList());
                    Map<Long, AssetDepreciationCalculationContext> calculationMap = AssetDepreciationAPI.getAssetDepreciationCalculation(lastCalculatedList)
                            .stream().collect(Collectors.toMap(AssetDepreciationCalculationContext::getId, Function.identity()));

                    List<AssetDepreciationCalculationContext> newList = new ArrayList<>();
                    for (AssetDepreciationRelContext assetDepreciationRelContext : assetDepreciationRelList) {
                        AssetContext assetInfo = assetMap.get(assetDepreciationRelContext.getAssetId());
                        if (assetInfo == null) {
                            continue;
                        }
                        List<AssetDepreciationCalculationContext> newDepreciationCalculation =
                                calculateAssetDepreciation(assetDepreciationContext, assetInfo, calculationMap.get(assetDepreciationRelContext.getLastCalculatedId()));
                        if (CollectionUtils.isNotEmpty(newDepreciationCalculation)) {
                            newList.addAll(newDepreciationCalculation);
                        }
                    }

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

                            asset.setCurrentPrice(context.getCurrentPrice());
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
            }
        }
    }

    private List<AssetDepreciationCalculationContext> calculateAssetDepreciation(AssetDepreciationContext assetDepreciation, AssetContext assetContext,
                                                                           AssetDepreciationCalculationContext lastCalculation) throws Exception {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);

            int totalPrice = (int) FieldUtil.getValue(assetContext, assetDepreciation.getTotalPriceFieldId(), assetModule);
            if (totalPrice == -1) {
                throw new IllegalArgumentException("Price cannot be empty");
            }
            int salvageAmount = (int) FieldUtil.getValue(assetContext, assetDepreciation.getSalvagePriceFieldId(), assetModule);
            long date = (long) FieldUtil.getValue(assetContext, assetDepreciation.getStartDateFieldId(), assetModule);
            int currentAmount = (int) FieldUtil.getValue(assetContext, assetDepreciation.getCurrentPriceFieldId(), assetModule);
            if (currentAmount == -1) {
                currentAmount = totalPrice;
            }

            if (lastCalculation != null) {
                date = lastCalculation.getCalculatedDate();
            }

            if (date == -1) {
                throw new IllegalArgumentException("Start date cannot be empty");
            }

            List<AssetDepreciationCalculationContext> list = new ArrayList<>();
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

                int newPrice = ((Number) assetDepreciation.getDepreciationTypeEnum().nextDepreciatedUnitPrice(totalDepreciationAmount, assetDepreciation.getFrequency(), currentAmount)).intValue();

                AssetDepreciationCalculationContext newlyCalculated = new AssetDepreciationCalculationContext();
                newlyCalculated.setAsset(assetContext);
                newlyCalculated.setCalculatedDate(nextDate);
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
}
