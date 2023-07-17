package com.facilio.bmsconsole.context;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
@Getter@Setter
public class WorkflowRuleActionLogContext extends V3Context {
    public int getActionType() {
        if(actionType!=null) {
            return actionType.getVal();
        }
        return -1;
    }
    public void setActionType(int type) {
        this.actionType = ActionType.getActionType(type);
    }
    public void setActionType(ActionType type) {
        this.actionType = type;
    }
    public ActionType getActionTypeEnum(){
        return actionType;
    }
    private long workflowRuleLogId;
    private ActionType actionType;
    public void setActionStatus(int status){
        this.actionStatus=ActionStatus.valueOf(status);
    }
    public void setActionStatus(ActionStatus status){
        this.actionStatus=status;
    }
    public int getActionStatus(){
        if(actionStatus!=null) {
            return actionStatus.getIndex();
        }
        return -1;
    }
    public ActionStatus getActionStatusEnum()
    {
        return this.actionStatus;
    }
    private ActionStatus actionStatus;
    public WorkflowRuleActionLogContext(ActionType actionType,ActionStatus  actionStatus){
        this.actionType=actionType;
        this.actionStatus=actionStatus;
    }
    public WorkflowRuleActionLogContext(){
    }
    public enum ActionStatus implements FacilioEnum {
        FAILED("Failed"), SKIPPED("Skipped"),SUCCESS("Success");
        private String name;
        ActionStatus(String name) {
            this.name = name;
        }
        @Override
        public String getValue() {
            return name;
        }
        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }
        public static ActionStatus valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}