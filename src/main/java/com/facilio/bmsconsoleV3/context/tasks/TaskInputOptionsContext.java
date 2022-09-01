package com.facilio.bmsconsoleV3.context.tasks;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInputOptionsContext extends V3Context {

    public TaskInputOptionsContext(){}

    public TaskInputOptionsContext(String value, String label, long sequence, JobPlanTasksContext jobPlanTask) {
        this.value = value;
        this.label = label;
        this.sequence = sequence;
        this.jobPlanTask = jobPlanTask;
    }

    private String value;
    private String label;
    private long sequence;
    private JobPlanTasksContext jobPlanTask;

}
