package com.facilio.readingrule.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.v3.V3Action;
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
    private String moduleName;
    private Long alarmId;
    private Integer dateOperator;
    private String dateOperatorValue;
    private String filters;

    public String rcaRuleList() throws Exception {
        FacilioChain chain = TransactionChainFactory.fetchRcaRules();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        List<NewReadingRuleContext> newReadingRuleContext = (List<NewReadingRuleContext>) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE_LIST);
        setData("result", newReadingRuleContext);
        return SUCCESS;
    }
    public String rcaReadingsFetch() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.fetchRcaReadingsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, alarmId);
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, getDateOperator());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, getDateOperatorValue());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS_MODULE);
        String filters = getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        chain.execute();
        List<RCAScoreReadingContext> rcaScoreReadingContexts = (List<RCAScoreReadingContext>) context.get(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS);
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.RECORD_LIST, rcaScoreReadingContexts);
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        setData(FacilioConstants.ContextNames.RESULT, result);
        setData("result", result);
        return SUCCESS;
    }
}
