package com.facilio.timeline.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;

import java.util.List;

public class TimelineRequest {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private List<Object> groupIds;
    public List<Object> getGroupIds() {
        return groupIds;
    }
    public void setGroupIds(List<Object> groupIds) {
        this.groupIds = groupIds;
    }

    private Long startTime;
    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    private Long endTime;
    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    private BmsAggregateOperators.DateAggregateOperator dateAggregateOperator;
    public void setDateAggregateOperator(Integer dateAggregateOperator) {
        AggregateOperator aggregateOperator = AggregateOperator.getAggregateOperator(dateAggregateOperator);
        if (aggregateOperator instanceof BmsAggregateOperators.DateAggregateOperator) {
            this.dateAggregateOperator = (BmsAggregateOperators.DateAggregateOperator) aggregateOperator;
        }
    }
    public Integer getDateAggregateOperator() {
        return dateAggregateOperator.getValue();
    }
    public BmsAggregateOperators.DateAggregateOperator getDateAggregator() {
        return dateAggregateOperator;
    }

    private int maxResultPerCell = 5;
    public int getMaxResultPerCell() {
        return maxResultPerCell;
    }
    public void setMaxResultPerCell(int maxResultPerCell) {
        this.maxResultPerCell = maxResultPerCell;
    }
}
