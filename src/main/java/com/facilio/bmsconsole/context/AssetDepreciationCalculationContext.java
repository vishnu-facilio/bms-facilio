package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.Serializable;

public class AssetDepreciationCalculationContext extends ModuleBaseWithCustomFields {

    private AssetContext asset;
    public AssetContext getAsset() {
        return asset;
    }
    public void setAsset(AssetContext asset) {
        this.asset = asset;
    }

    private long depreciationId = -1;
    public long getDepreciationId() {
        return depreciationId;
    }
    public void setDepreciationId(long depreciationId) {
        this.depreciationId = depreciationId;
    }

    private long calculatedDate = -1;
    public long getCalculatedDate() {
        return calculatedDate;
    }
    public void setCalculatedDate(long calculatedDate) {
        this.calculatedDate = calculatedDate;
    }

    private int currentPrice = -1;
    public int getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    private long depreciatedAmount = -1;
    public long getDepreciatedAmount() {
        return depreciatedAmount;
    }
    public void setDepreciatedAmount(long depreciatedAmount) {
        this.depreciatedAmount = depreciatedAmount;
    }
}
