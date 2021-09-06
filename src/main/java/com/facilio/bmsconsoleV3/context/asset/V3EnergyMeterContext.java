package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;

import java.util.List;

public class V3EnergyMeterContext extends V3AssetContext {

    private static final long serialVersionUID = 1L;

    private V3EnergyMeterPurposeContext purpose;
    private Boolean root;
    private V3BaseSpaceContext purposeSpace;
    private Boolean isVirtual;
    private Boolean isHistoricalRunning;
    private String childMeterExpression;
    private List<Long> childMeterIds;
    private Double multiplicationFactor;

    public V3EnergyMeterPurposeContext getPurpose() {
        return purpose;
    }

    public void setPurpose(V3EnergyMeterPurposeContext purpose) {
        this.purpose = purpose;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public V3BaseSpaceContext getPurposeSpace() {
        return purposeSpace;
    }

    public void setPurposeSpace(V3BaseSpaceContext purposeSpace) {
        this.purposeSpace = purposeSpace;
    }

    public Boolean getVirtual() {
        return isVirtual;
    }

    public void setVirtual(Boolean virtual) {
        isVirtual = virtual;
    }

    public Boolean getHistoricalRunning() {
        return isHistoricalRunning;
    }

    public void setHistoricalRunning(Boolean historicalRunning) {
        isHistoricalRunning = historicalRunning;
    }

    public String getChildMeterExpression() {
        return childMeterExpression;
    }

    public void setChildMeterExpression(String childMeterExpression) {
        this.childMeterExpression = childMeterExpression;
    }

    public List<Long> getChildMeterIds() {
        return childMeterIds;
    }

    public void setChildMeterIds(List<Long> childMeterIds) {
        this.childMeterIds = childMeterIds;
    }

    public Double getMultiplicationFactor() {
        return multiplicationFactor;
    }

    public void setMultiplicationFactor(Double multiplicationFactor) {
        this.multiplicationFactor = multiplicationFactor;
    }



}
