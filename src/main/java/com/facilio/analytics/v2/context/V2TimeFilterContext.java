package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.time.DateRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2TimeFilterContext {

    public Integer offset;
    public DateOperators dateOperator;
    private DateRange dateRange;
    public String dateLabel;
    private long startTime = -1;
    private long endTime = -1;
    public String baselinePeriod;
    public String dateValueString;
    public DateRange baselineRange;
    public String label;
    public int getDateOperator() {
        return dateOperator != null ? ((DateOperators)dateOperator).getOperatorId() : -1;
    }

    public void setDateOperator(int dateOperator) {
        this.dateOperator = (DateOperators) Operator.getOperator(dateOperator);
    }
    public DateOperators getDateOperatorEnum(){
        return dateOperator;
    }
    public void setDateOperatorEnum(DateOperators operator){
        this.dateOperator = operator;
    }

}
