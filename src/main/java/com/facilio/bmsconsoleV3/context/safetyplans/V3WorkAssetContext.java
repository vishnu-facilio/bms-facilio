package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.v3.context.V3Context;

public class V3WorkAssetContext extends V3Context {
    private V3BaseSpaceContext space;
    private V3AssetContext asset;

    public V3BaseSpaceContext getSpace() {
        return space;
    }

    public void setSpace(V3BaseSpaceContext space) {
        this.space = space;
    }

    public V3AssetContext getAsset() {
        return asset;
    }

    public void setAsset(V3AssetContext asset) {
        this.asset = asset;
    }

    public V3SafetyPlanContext getSafetyPlan() {
        return safetyPlan;
    }

    public void setSafetyPlan(V3SafetyPlanContext safetyPlan) {
        this.safetyPlan = safetyPlan;
    }

    private V3SafetyPlanContext safetyPlan;

}
