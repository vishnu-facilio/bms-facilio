package com.facilio.bmsconsole.context;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsole.context.WorkflowRuleLogContext.WorkflowLoggableRuleType.ruleTypeMap;

@Getter @Setter
public  class WorkflowRuleLogContext extends V3Context {
    public static final Logger LOGGER = LogManager.getLogger(WorkflowRuleLogContext.class.getName());
    private String recordModuleName;
    private String workflowRuleName;
    private WorkflowLoggableRuleType workflowLoggableRuleType;
    public int getWorkflowLoggableRuleType() {
        if (workflowLoggableRuleType != null) {
            return workflowLoggableRuleType.getTypeId();
        }
        return -1;
    }
    public void setWorkflowLoggableRuleType(int type) {
        this.workflowLoggableRuleType = WorkflowLoggableRuleType.typeMap.get(type);
    }
    public void setWorkflowLoggableRuleType(WorkflowLoggableRuleType type) {
        this.workflowLoggableRuleType =type;
    }
    public WorkflowLoggableRuleType getWorkflowLoggableRuleTypeEnum(){
        return workflowLoggableRuleType;
    }

    public enum WorkflowLoggableRuleType implements FacilioIntEnum {

        MODULE_RULE(1, WorkflowRuleContext.RuleType.MODULE_RULE,"Module Rule"),
        STATE_RULE(2, WorkflowRuleContext.RuleType.STATE_RULE, "State Rule"),
        CUSTOM_BUTTON(3, WorkflowRuleContext.RuleType.CUSTOM_BUTTON,"Custom Button Rule"),
        APPROVAL_STATE_FLOW(4, WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW,"Approval Stateflow Rule"),
        APPROVAL_STATE_TRANSITION(5, WorkflowRuleContext.RuleType.APPROVAL_STATE_TRANSITION,"Approval State Transition Rule"),
        MODULE_RULE_NOTIFICATION(6,WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION,"Module Rule Notification"),
        STATE_FLOW(7,WorkflowRuleContext.RuleType.STATE_FLOW,"State Flow"),
        ASSIGNMENT_RULE(8, WorkflowRuleContext.RuleType.ASSIGNMENT_RULE,"Assignment Rule"),
        SLA_WORKFLOW_RULE(9,WorkflowRuleContext.RuleType.SLA_WORKFLOW_RULE,"Sla Workflow Rule"),
        SLA_POLICY_RULE(10, WorkflowRuleContext.RuleType.SLA_POLICY_RULE,"Sla Policy Rule")
        ;


        WorkflowLoggableRuleType(int i, WorkflowRuleContext.RuleType ruleType, String name) {
            this.typeId=i;
            this.ruleType = ruleType;
            this.name= name;
        }

        public WorkflowRuleContext.RuleType getRuleType() {
            return ruleType;
        }

        public void setRuleType(WorkflowRuleContext.RuleType ruleType) {
            this.ruleType = ruleType;
        }


        private int typeId;
        private WorkflowRuleContext.RuleType ruleType;

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
        public static final Map<Integer, WorkflowLoggableRuleType> typeMap = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, WorkflowLoggableRuleType> initTypeMap() {
            Map<Integer, WorkflowLoggableRuleType> typeMap = new HashMap<>();

            for(WorkflowLoggableRuleType type : values()) {
                typeMap.put(type.getTypeId(), type);
            }
            return typeMap;
        }
        public static final Map<WorkflowRuleContext.RuleType, WorkflowLoggableRuleType> ruleTypeMap = Collections.unmodifiableMap(initRuleTypeMap());
        private static Map<WorkflowRuleContext.RuleType, WorkflowLoggableRuleType> initRuleTypeMap() {
            Map<WorkflowRuleContext.RuleType, WorkflowLoggableRuleType> RuleTypeMap = new HashMap<>();

            for(WorkflowLoggableRuleType type : values()) {
                RuleTypeMap.put(type.getRuleType(), type);
            }
            return RuleTypeMap;
        }
    }


    private long workflowRuleId = -1;
    private long recordId = -1;
    private long executedOn = -1;
    private Boolean siteResult;
    private Boolean fieldChangeResult;
    private Boolean miscResult;
    private Boolean criteriaResult;
    private long recordModuleId = -1;
    private Boolean workflowResult;
    public WorkflowRuleLogContext() {
    }
    public User getExecutedBy() {
        return executedBy;
    }
    public void setExecutedBy(User executedBy) throws Exception {
        this.executedBy= AccountUtil.getUserBean().getUser(executedBy.getOuid(), false);
    }
    private User executedBy;
    private List<WorkflowRuleActionLogContext> actions;
    private String linkConfig;
    public String getLinkConfig() {
        return linkConfig;
    }
    @JsonInclude
    public JSONArray getLinkConfigJSON() {
        if (StringUtils.isNotEmpty(linkConfig)) {
            try {
                JSONParser parser = new JSONParser();
                return (JSONArray) parser.parse(linkConfig);
            } catch (ParseException e) {}
        }
        return new JSONArray();
    }
    public WorkflowRuleLogContext setLinkConfig(String linkConfig) {
        this.linkConfig = linkConfig;
        return this;
    }

    public void setRuleStatus(int status){
        this.ruleStatus = WorkflowRuleLogContext.RuleStatus.valueOf(status);
    }
    public void setRuleStatus(WorkflowRuleLogContext.RuleStatus status){
        this.ruleStatus =status;
    }
    public int getRuleStatus(){
        if(ruleStatus !=null) {
            return ruleStatus.getIndex();
        }
        return -1;
    }
    public WorkflowRuleLogContext.RuleStatus getRuleStatusEnum()
    {
        return this.ruleStatus;
    }
    private WorkflowRuleLogContext.RuleStatus ruleStatus;

    public enum RuleStatus implements FacilioEnum {
        FAILED("Failed"), WARNING("Warning"),SUCCESS("Success"),NO_ACTIONS("No Actions");
        private String name;
        RuleStatus(String name) {
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
        public static WorkflowRuleLogContext.RuleStatus valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }


    public WorkflowRuleLogContext(long recordId, WorkflowRuleContext workflowRule, boolean workflowResult, boolean fieldChangeResult,
                                  boolean siteResult,boolean miscResult,boolean criteriaResult, List<WorkflowRuleActionLogContext> actionLogList,String linkConfig) throws Exception {
        this.recordId = recordId;
        this.workflowRuleId = workflowRule.getId();
        this.recordModuleId = workflowRule.getModuleId();
        this.workflowResult = workflowResult;
        this.fieldChangeResult = fieldChangeResult;
        this.siteResult = siteResult;
        this.miscResult = miscResult;
        this.criteriaResult = criteriaResult;
        this.workflowRuleName=workflowRule.getName();
        this.workflowLoggableRuleType=ruleTypeMap.get(workflowRule.getRuleTypeEnum());
        this.actions=actionLogList;
        this.linkConfig=linkConfig;
        this.ruleStatus=this.generateRuleStatus(workflowResult,actionLogList);
    }

    private RuleStatus generateRuleStatus( boolean workflowResult,List<WorkflowRuleActionLogContext> actionLogList ){
        if(!workflowResult)
        {
            return RuleStatus.FAILED;
        }else if(CollectionUtils.isEmpty(actionLogList)){
            return RuleStatus.NO_ACTIONS;
        }
        else{
            int statusCheck=1;
            for(WorkflowRuleActionLogContext log:actionLogList){
                if(!(log.getActionStatusEnum()==WorkflowRuleActionLogContext.ActionStatus.SUCCESS)){
                    statusCheck=0;
                }
            }
            return statusCheck==1?RuleStatus.SUCCESS:RuleStatus.WARNING;
        }
    }
    public JSONObject toJson() throws Exception{
        try {
            if (executedBy ==null) {
                executedBy = AccountUtil.getCurrentUser();
            }
            if (executedOn <= 0) {
                executedOn = System.currentTimeMillis();
            }
            JSONObject json=FieldUtil.getAsJSON(this);
            return json;
        }catch (Exception e){
            LOGGER.error(e);
        }
        return null;
    }
}