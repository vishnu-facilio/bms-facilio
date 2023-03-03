package com.facilio.bmsconsole.calendarview;

import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.AggregateOperator;
import org.json.simple.JSONObject;

import java.util.List;

public class CalendarViewRequestContext {
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
