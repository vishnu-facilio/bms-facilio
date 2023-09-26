package com.facilio.analytics.v2.context;

import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.time.DateRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2TimeFilterContext {

    public int date_operator;
    public Integer date_offset;
    public DateOperators dateOperator;
    private DateRange dateRange;
    private long startTime = -1;
    private long endTime = -1;

    public int getDateOperator() {
        return dateOperator != null ? ((DateOperators)dateOperator).getOperatorId() : -1;
    }

    public void setDateOperator(int dateOperator) {
        this.dateOperator = (DateOperators) Operator.getOperator(dateOperator);
    }
    public DateOperators getDateOperatorEnum(){
        return dateOperator;
    }

}
