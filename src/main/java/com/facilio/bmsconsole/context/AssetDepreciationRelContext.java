package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class AssetDepreciationRelContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long assetId = -1;
    public long getAssetId() {
        return assetId;
    }
    public void setAssetId(long assetId) {
        this.assetId = assetId;
    }

    private long depreciationId = -1;
    public long getDepreciationId() {
        return depreciationId;
    }
    public void setDepreciationId(long depreciationId) {
        this.depreciationId = depreciationId;
    }

    private long depreciationAmount = -1;
    public long getDepreciationAmount() {
        return depreciationAmount;
    }
    public void setDepreciationAmount(long depreciationAmount) {
        this.depreciationAmount = depreciationAmount;
    }

    private Boolean activated = false;
    public Boolean getActivated() {
        return activated;
    }
    public void setActivated(Boolean activated) {
        this.activated = activated;
    }
}
