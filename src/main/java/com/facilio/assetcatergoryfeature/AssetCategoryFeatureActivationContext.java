package com.facilio.assetcatergoryfeature;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class AssetCategoryFeatureActivationContext implements Serializable {


    Long categoryId;

    Boolean readingRulesStatus;

    Boolean sensorRulesStatus;

    Boolean setPointStatus;


    public AssetCategoryFeatureActivationContext(Long categoryId) {
        this.categoryId = categoryId;
        this.readingRulesStatus = Boolean.TRUE;
        this.sensorRulesStatus = Boolean.TRUE;
        this.setPointStatus =  Boolean.TRUE;
    }

    public AssetCategoryFeatureActivationContext() {
    }
}
