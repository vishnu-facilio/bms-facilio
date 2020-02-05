package com.facilio.bmsconsole.workflow.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SLAWorkflowEscalationContext implements Serializable {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long slaPolicyId;
    public long getSlaPolicyId() {
        return slaPolicyId;
    }
    public void setSlaPolicyId(long slaPolicyId) {
        this.slaPolicyId = slaPolicyId;
    }

    private WorkflowRuleContext.ScheduledRuleType type;
    public WorkflowRuleContext.ScheduledRuleType getTypeEnum() {
        return type;
    }
    public int getType() {
        if (type != null) {
            return type.getValue();
        }
        return -1;
    }
    public void setType(int typeInt) {
        this.type = WorkflowRuleContext.ScheduledRuleType.valueOf(typeInt);
    }
    public void setType(WorkflowRuleContext.ScheduledRuleType type) {
        this.type = type;
    }

    private long interval = -1l;
    public long getInterval() {
        return interval;
    }
    public void setInterval(long interval) {
        this.interval = interval;
    }

    private List<ActionContext> actions;
    public List<ActionContext> getActions() {
        return actions;
    }
    public void setActions(List<ActionContext> actions) {
        this.actions = actions;
    }

    public void addAction(ActionContext actionContext) {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(actionContext);
    }
}
