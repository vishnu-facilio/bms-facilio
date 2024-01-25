package com.facilio.workflowlog.context;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Log4j
public class WorkflowLogContext extends V3Context {

    long recordId;
    long parentId;
    long workflowId;
    public WorkflowLogType logType;
    public WorkflowLogStatus status;
    String exception;
    long createdTime;
    String logValue;
    private Long recordModuleId;

    // for client purpose
    private String recordModuleName;
    private String workflowRuleName;
    private boolean logAvailable;
    private boolean exceptionAvailable;

    private int totalStatementCount=0;
    private int totalSelectCount=0;
    private int totalInsertCount=0;
    private int totalUpdateCount=0;
    private int totalDeleteCount=0;
    private int totalApiRequestCount=0;
    private int totalApiResponseCount=0;
    private int totalEmailCount=0;
    private int totalPushNotficationCount=0;
    private String threadName;

    public void setLogAvailable(boolean value){
        logAvailable = value;
    }

    public void setExceptionAvailable(boolean value){
        exceptionAvailable = value;
    }

    public int getStatus() {
        return status == null ? -1 :status.getStatusId();
    }
    public void setStatus(int status) {
        this.status = (status <=0 ? null : WorkflowLogStatus.typeMap.get(status));
    }

    public int getLogType() {
        return logType == null ? -1 : logType.getTypeId();
    }
    public void setLogType(int type) {
        this.logType = (type <= 0 ? null: WorkflowLogType.typeMap.get(type));
    }
    public void setLogType(WorkflowLogType type) {
        this.logType = type;
    }


    public enum WorkflowLogType implements FacilioIntEnum{
        FORMULA(1,"Formula"),
        SCHEDULER(2,"Scheduler"),
        MODULE_RULE(3,RuleType.MODULE_RULE,"Module Rule"),
        STATE_RULE(4,RuleType.STATE_RULE, "State Rule"),
        WORKFLOW_RULE_EVALUATION(5,"Workflow Rule"),
        Q_AND_A_RULE(6,"Q and A Rule"),
        CUSTOM_BUTTON_RULE(7, RuleType.CUSTOM_BUTTON,"Custom Button Rule"),
        APPROVAL_STATE_FLOW(8,RuleType.APPROVAL_STATE_FLOW,"Stateflow Rule"),
        APPROVAL_STATE_TRANSITION(9,RuleType.APPROVAL_STATE_TRANSITION,"State Transition Rule"),
        NOTIFICATION_RULE(10,RuleType.MODULE_RULE_NOTIFICATION,"Notification Rule"),
        FLOW(11,RuleType.FLOW,"Flow")
        ;

        WorkflowLogType(int i,String name) {
            this.typeId = i;
            this.name= name;
        }


        WorkflowLogType(int i, RuleType ruleType, String name) {
            this.typeId=i;
            this.ruleType = ruleType;
            this.name= name;
        }


        public RuleType getRuleType() {
            return ruleType;
        }


        public void setRuleType(RuleType ruleType) {
            this.ruleType = ruleType;
        }


        private int typeId;
        private RuleType ruleType;

        private String name;


        @Override
        public String getValue() {
            return name;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
        public static final Map<Integer, WorkflowLogType> typeMap = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, WorkflowLogType> initTypeMap() {
            Map<Integer, WorkflowLogType> typeMap = new HashMap<>();

            for(WorkflowLogType type : values()) {
                typeMap.put(type.getTypeId(), type);
            }
            return typeMap;
        }
        public static final Map<RuleType, WorkflowLogType> ruleTypeMap = Collections.unmodifiableMap(initRuleTypeMap());
        private static Map<RuleType, WorkflowLogType> initRuleTypeMap() {
            Map<RuleType, WorkflowLogType> RuleTypeMap = new HashMap<>();

            for(WorkflowLogType type : values()) {
                RuleTypeMap.put(type.getRuleType(), type);
            }
            return RuleTypeMap;
        }
    }

    public enum WorkflowLogStatus implements FacilioIntEnum{
        SUCCESS(1,"Success"),
        FAILURE(2, "Failure"),
        SYNTAX_ERROR(3,"Syntax Error")
        ;

        WorkflowLogStatus(int i, String name) {
            this.statusId = i;
            this.name = name;
        }

        private int statusId;

        private String name;

        @Override
        public String getValue() {
            return name;
        }
        public void setStatusId(int typeId) {
            this.statusId = typeId;
        }

        public int getStatusId() {
            return statusId;
        }
        public static final Map<Integer, WorkflowLogStatus> typeMap = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, WorkflowLogStatus> initTypeMap() {
            Map<Integer, WorkflowLogStatus> typeMap = new HashMap<>();

            for(WorkflowLogStatus status : values()) {
                typeMap.put(status.getStatusId(), status);
            }
            return typeMap;
        }
    }
}
