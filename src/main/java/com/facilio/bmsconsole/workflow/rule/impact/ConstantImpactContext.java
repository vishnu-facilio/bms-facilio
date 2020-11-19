package com.facilio.bmsconsole.workflow.rule.impact;

public class ConstantImpactContext extends BaseAlarmImpactContext {

    private float constant = -1;
    public float getConstant() {
        return constant;
    }
    public void setConstant(float constant) {
        this.constant = constant;
    }

    @Override
    protected void validateImpact() throws Exception {
        if (constant < 0) {
            throw new IllegalArgumentException("Constant cannot be empty");
        }
    }

    @Override
    public float calculateImpact() throws Exception {
        return constant;
    }
}
