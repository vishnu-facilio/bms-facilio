package com.facilio.readingrule.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;


@Log4j
@Setter
@Getter
public class ReadingRuleAction extends FacilioAction {

    private String isCount;

    private long ruleId = -1;


    public String execute() {
        return SUCCESS;
    }

    NewReadingRuleContext readingRule;


    public String addNewReadingRule() throws Exception {

        FacilioChain chain = TransactionChainFactory.addReadingRule();

        FacilioContext ctx = chain.getContext();
        ctx.put(FacilioConstants.ContextNames.NEW_READING_RULE, readingRule);
        chain.execute();

        readingRule.setNullForResponse();
        setResult("result", readingRule);

        return SUCCESS;
    }

    public String v2RulesList() throws Exception {
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

        if (getSearch() != null) {
            JSONObject searchObj = new JSONObject();
            searchObj.put("fields", "workflowrule.name");
            searchObj.put("query", getSearch());
            context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
        }

        if (getPage() != 0) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", getPage());
            pagination.put("perPage", getPerPage());
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }

        System.out.println("request" + getIsCount());
        context.put(FacilioConstants.ContextNames.ID, ruleId);
        context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.READING_RULE_MODULE);

        FacilioChain workflowRuleType = ReadOnlyChainFactory.fetchReadingRules();
        workflowRuleType.execute(context);

//		workflowRuleList = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
//		if (getIsCount() != null) {
//			setCount((long) context.get(FacilioConstants.ContextNames.RULE_COUNT));
//		}
//		setResult("count", context.get(FacilioConstants.ContextNames.RULE_COUNT));

        List<NewReadingRuleContext> rules = (List<NewReadingRuleContext>) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        for(NewReadingRuleContext r : rules) {
            r.setNullForResponse();
        }

        setResult("rules", rules);
        return SUCCESS;

    }
}
