package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.v3.context.V3Context;

public class AssetSpareParts extends V3Context {

    private static final long serialVersionUID = 1L;
    private V3AssetContext asset;
    private V3ItemTypesContext itemType;
    private Integer requiredCount;
    private Integer issuedCount;
    private String remarks;

    public V3AssetContext getAsset() {
        return asset;
    }

    public void setAsset(V3AssetContext asset) {
        this.asset = asset;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public Integer getRequiredCount() {
        return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
        this.requiredCount = requiredCount;
    }

    public Integer getIssuedCount() {
        return issuedCount;
    }

    public void setIssuedCount(Integer issuedCount) {
        this.issuedCount = issuedCount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
