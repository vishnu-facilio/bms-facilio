package com.facilio.bmsconsoleV3.context.asset;

public class V3UtilityMeterContext extends V3AssetContext {

    private static final long serialVersionUID = 1L;

    private String assetNo;
    private String premise;
    private String location;
    private String consumerNumber;

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getPremise() {
        return premise;
    }

    public void setPremise(String premise) {
        this.premise = premise;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(String consumerNumber) {
        this.consumerNumber = consumerNumber;
    }


}
