package com.facilio.timeline.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;

import java.util.List;

public class TimelineRequest {

    private String viewName;
    public String getViewName() {
        return viewName;
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

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

    private boolean getUnGrouped = false;
    public boolean isGetUnGrouped() {
        return getUnGrouped;
    }
    public void setGetUnGrouped(boolean getUnGrouped) {
        this.getUnGrouped = getUnGrouped;
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

    public String getDateValue() {
        return startTime + "," + endTime;
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
