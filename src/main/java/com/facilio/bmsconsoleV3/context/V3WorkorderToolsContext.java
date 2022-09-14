package com.facilio.bmsconsoleV3.context;

public class V3WorkorderToolsContext extends V3ToolTransactionContext{
    private static final long serialVersionUID = 1L;

    private Double cost;

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
        this.setTransactionCost(cost);
    }

    private Long issueTime;

    public Long getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Long issueTime) {
        this.issueTime = issueTime;
    }

    private Long returnTime;

    public Long getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Long returnTime) {
        this.returnTime = returnTime;
    }

    private Double duration;

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getDuration() {
        return duration;
    }

    private Double rate;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
