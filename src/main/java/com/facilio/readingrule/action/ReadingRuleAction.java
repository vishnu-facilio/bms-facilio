package com.facilio.readingrule.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;


@Log4j
@Setter
@Getter
public class ReadingRuleAction extends V3Action {

    private String isCount;

    private long ruleId = -1;

    private boolean status;
    public boolean getStatus(){return status;}
    public void setStatus(boolean status){this.status=status;}

    private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

    NewReadingRuleContext readingRule;


    public String addNewReadingRule() throws Exception {

        FacilioChain chain = TransactionChainFactory.addReadingRuleChain();

        FacilioContext ctx = chain.getContext();
        ctx.put(FacilioConstants.ContextNames.NEW_READING_RULE, readingRule);
        chain.execute();

        readingRule.setNullForResponse();
        setData("result", readingRule);

        return SUCCESS;
    }

    public String v3RulesList() throws Exception {
        FacilioContext context = new FacilioContext();
        if (getFilters() != null) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
        }
        if (getIsCount() != null) {
            context.put(FacilioConstants.ContextNames.RULE_COUNT, getIsCount());
        }

        if (getPage() != 0) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", getPage());
            pagination.put("perPage", getPerPage());
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }

        context.put(FacilioConstants.ContextNames.ID, ruleId);
        context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.NEW_READING_RULE_MODULE);

        FacilioChain workflowRuleType = ReadOnlyChainFactory.fetchReadingRules();
        workflowRuleType.execute(context);


        if (context.get(FacilioConstants.ContextNames.RULE_COUNT) != null) {
            setData("count", context.get(FacilioConstants.ContextNames.RULE_COUNT));
        } else {
            List<NewReadingRuleContext> rules = (List<NewReadingRuleContext>) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
            for (NewReadingRuleContext r : rules) {
                r.setNullForResponse();
            }
            setData("rules", rules);
        }


        return SUCCESS;

    }

    public String fetchReadingRuleSummary() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ID, ruleId);
        context.put(FacilioConstants.ContextNames.IS_SUMMARY, true);

        FacilioChain fetchAlarmChain = ReadOnlyChainFactory.fetchReadingRuleSummaryChain();
        fetchAlarmChain.execute(context);
        AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
        alarmRule.setNullForResponse();
        setData("matchedassetcount", context.get(FacilioConstants.ContextNames.RULE_ASSET_COUNT));
        setData("matchedassetids", context.get(FacilioConstants.ContextNames.ASSET_LIST));
        setData("alarmRule", alarmRule);
        setData("module", context.get(FacilioConstants.ContextNames.MODULE));
        return SUCCESS;
    }

    public String deleteReadingRule() throws Exception {

        NewReadingRuleContext rule = NewReadingRuleAPI.getRule(ruleId);
        V3Util.throwRestException(rule == null, ErrorCode.VALIDATION_ERROR, String.format("Rule ({0}) is not found", ruleId));

        FacilioChain chain = TransactionChainFactory.deleteReadingRuleChain();
        FacilioContext ctx = chain.getContext();
        ctx.put(FacilioConstants.ContextNames.NEW_READING_RULE, rule);
        ctx.put(NamespaceConstants.NAMESPACE, rule.getNs());
        ctx.put(WorkflowV2Util.WORKFLOW_CONTEXT, rule.getWorkflowContext());
        chain.execute();
        setData("result", true);
        return SUCCESS;
    }

    public String updateReadingRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.updateReadingRuleChain();
        FacilioContext ctx = chain.getContext();
        ctx.put(FacilioConstants.ContextNames.NEW_READING_RULE, readingRule);
        chain.execute();

        NewReadingRuleContext ruleContext = (NewReadingRuleContext) ctx.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        ruleContext.setNullForResponse();

        setData("result", ruleContext);
        return SUCCESS;

    }

    public String changeReadingRuleStatus() throws Exception {
        NewReadingRuleContext newReadingRuleContext = new NewReadingRuleContext();
        newReadingRuleContext.setStatus(getStatus());
        newReadingRuleContext.setId(getRuleId());
        NewReadingRuleAPI.updateReadingRuleStatus(newReadingRuleContext);
        setData("result", "success");
        return SUCCESS;
    }

}
