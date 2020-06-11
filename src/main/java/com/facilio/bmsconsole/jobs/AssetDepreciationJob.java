package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                                AssetDepreciationAPI.calculateAssetDepreciation(assetDepreciationContext, assetInfo, calculationMap.get(assetDepreciationRelContext.getLastCalculatedId()));
                        if (CollectionUtils.isNotEmpty(newDepreciationCalculation)) {
                            newList.addAll(newDepreciationCalculation);
                        }
                    }

                    AssetDepreciationAPI.saveDepreciationCalculationList(newList, assetDepreciationContext);
                }
            }
        }
    }
}
