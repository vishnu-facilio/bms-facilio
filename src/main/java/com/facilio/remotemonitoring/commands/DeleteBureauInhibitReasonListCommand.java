package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.remotemonitoring.context.BureauInhibitReasonListContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteBureauInhibitReasonListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleBureauEvaluationContext> evaluationContexts = (List<FlaggedEventRuleBureauEvaluationContext>) recordMap.get(FlaggedEventBureauEvaluationModule.MODULE_NAME);
        if (CollectionUtils.isNotEmpty(evaluationContexts)) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            if (CollectionUtils.isNotEmpty(recordIds)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_BUREAU_EVALUATION", "flaggedEventBureauEvaluation", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                V3RecordAPI.deleteRecords(BureauInhibitReasonListModule.MODULE_NAME, criteria, false);
                deleteBureauResolutionList(recordIds);
                deleteBureauCauseList(recordIds);
                deleteCloseIssueReasonListOption(recordIds);
            }
        }
        return false;
    }

    private static void deleteBureauResolutionList(List<Long> recordIds) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
        V3RecordAPI.deleteRecords(BureauResolutionListModule.MODULE_NAME, criteria, false);
    }

    private static void deleteBureauCauseList(List<Long> recordIds) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
        V3RecordAPI.deleteRecords(BureauCauseListModule.MODULE_NAME, criteria, false);
    }

    private static void deleteCloseIssueReasonListOption(List<Long> recordIds) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
        V3RecordAPI.deleteRecords(BureauCloseIssueReasonOptionListModule.MODULE_NAME, criteria, false);
    }
}
