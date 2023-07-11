package com.facilio.remotemonitoring.context;

import com.facilio.accounts.dto.Group;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class FlaggedEventRuleBureauEvaluationContext extends V3Context {
    private Long emailRuleId;
    private FlaggedEventRuleContext flaggedEventRule;
    private WorkflowRuleContext emailRule;
    private Long takeCustodyPeriod;
    private Long takeActionPeriod;
    private Boolean addUnusedEvalTime;
    private List<BureauInhibitReasonListContext> inhibitReasonList;
    private Group team;
    private List<BureauCloseIssueReasonOptionContext> closeIssueReasonOptionContexts;
    private List<BureauCauseListContext> causeList;
    private List<BureauResolutionListContext> resolutionList;
    private Long amberUrgencyTime;
    private Long redUrgencyTime;
    private Boolean isFinalTeam;
    private Integer order;
    public boolean finalTeam(){
        if(isFinalTeam == null) {
            return false;
        }
        return isFinalTeam;
    }
}