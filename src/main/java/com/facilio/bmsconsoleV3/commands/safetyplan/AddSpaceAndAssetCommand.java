package com.facilio.bmsconsoleV3.commands.safetyplan;


import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3WorkAssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;


import java.util.List;
import java.util.Map;

public class AddSpaceAndAssetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<V3WorkAssetContext> workAssetList = recordMap.get(FacilioConstants.ContextNames.WORK_ASSET);
        if(CollectionUtils.isNotEmpty(workAssetList)){
            V3WorkAssetContext workAsset = workAssetList.get(0);
            V3AssetContext assets = workAsset.getAsset();
            V3BaseSpaceContext spaces = workAsset.getSpace();
            V3SafetyPlanContext safetyPlan = workAsset.getSafetyPlan();
            if(safetyPlan != null) {
                Long assetId = assets.getId();
                Long spaceId = spaces.getId();
                Long safetyPlanId = safetyPlan.getId();
                if (checkIfResourceAvailable(assetId,spaceId, safetyPlanId)) {
                    throw new IllegalArgumentException("Work Asset already available for this record");
                }else if (assetId != null && assetId > 0 && spaceId!=null && spaceId > 0 ){
                    throw new IllegalArgumentException("Either Asset or Space only be chosen");
                }
            }
        }
        return false;
    }
    public static boolean checkIfResourceAvailable(long assetId,long spaceId,long safetyPlanId) throws Exception {
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(assetId), NumberOperators.EQUALS);
        Condition condition_1 = CriteriaAPI.getCondition("BASESPACE_ID", "space", String.valueOf(spaceId), NumberOperators.EQUALS);
        Condition condition_2 = CriteriaAPI.getCondition("SAFETY_PLAN_ID", "safetyPlan", String.valueOf(safetyPlanId), NumberOperators.EQUALS);
        criteria.addOrCondition(condition);
        criteria.addOrCondition(condition_1);
        criteria.addAndCondition(condition_2);
        Map<Long, V3WorkAssetContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORK_ASSET, null, V3WorkAssetContext.class, criteria, null);
        if(props != null && props.size() > 0 ){
            return true;
        }
        return false;
    }
}
