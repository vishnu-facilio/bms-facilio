package com.facilio.readingrule.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSType;
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
    String moduleName;

    public String rcaRuleList() throws Exception{
        FacilioChain chain = TransactionChainFactory.fetchRcaRules();
        FacilioContext context =chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        chain.execute();
        List<NewReadingRuleContext> newReadingRuleContext= (List<NewReadingRuleContext>) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE_LIST);
        setData("result",newReadingRuleContext);
        return SUCCESS;
    }
}
