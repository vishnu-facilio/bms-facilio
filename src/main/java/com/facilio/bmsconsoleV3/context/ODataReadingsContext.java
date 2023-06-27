package com.facilio.bmsconsoleV3.context;

import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ODataReadingsContext {
    private String name;
    private String displayName;
    private String description;
    private boolean isEnabled ;
    private long dateOperator;
    private long categorymoduleId;
    private long aggregateOperator;
    private long readingType;
    private String dateRange;
    private String readingFields;
    private Criteria criteria;
    private long criteriaId;
}
