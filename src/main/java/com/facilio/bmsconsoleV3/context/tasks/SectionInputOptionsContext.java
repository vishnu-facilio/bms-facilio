package com.facilio.bmsconsoleV3.context.tasks;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionInputOptionsContext extends V3Context {

    public SectionInputOptionsContext(){}

    public SectionInputOptionsContext(String value, String label, long sequence, JobPlanTaskSectionContext jobPlanSection) {
        this.value = value;
        this.label = label;
        this.sequence = sequence;
        this.jobPlanSection = jobPlanSection;
    }

    private String value;
    private String label;
    private long sequence;
    private JobPlanTaskSectionContext jobPlanSection;

}
