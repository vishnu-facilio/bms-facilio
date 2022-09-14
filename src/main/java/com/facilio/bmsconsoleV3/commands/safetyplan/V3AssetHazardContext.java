package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardContext;
import com.facilio.v3.context.V3Context;

public class V3AssetHazardContext extends V3Context {
    private V3AssetContext asset;
    private V3HazardContext hazard;
    public V3HazardContext getHazard() {
        return hazard;
    }
    public void setHazard(V3HazardContext hazard) {
        this.hazard = hazard;
    }
    public V3AssetContext getAsset() {
        return asset;
    }
    public void setAsset(V3AssetContext asset) {
        this.asset = asset;
    }
}
