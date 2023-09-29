package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FlaggedEventBureauInformationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for (FlaggedEventContext flaggedEvent : flaggedEvents) {
                if (flaggedEvent.getFlaggedEventRule() != null) {
                    List<FlaggedEventRuleBureauEvaluationContext> bureauEvaluations = getBureauEvaluation(flaggedEvent.getFlaggedEventRule().getId());
                    if (CollectionUtils.isNotEmpty(bureauEvaluations)) {
                        List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
                        for (FlaggedEventRuleBureauEvaluationContext evaluationContext : bureauEvaluations) {
                            FlaggedEventBureauActionsContext bureauActionsContext = FieldUtil.cloneBean(evaluationContext, FlaggedEventBureauActionsContext.class);
                            FlaggedEventContext event = new FlaggedEventContext();
                            event.setId(flaggedEvent.getId());
                            bureauActionsContext.setFlaggedEvent(event);
                            bureauActionsContext.setEventStatus(FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus.NOT_STARTED);
                            bureauActionsContext.setResolutionList(getBureauResolutionList(bureauActionsContext.getId()));
                            bureauActionsContext.setCauseList(getBureauCauseList(bureauActionsContext.getId()));
                            bureauActionsContext.setCloseIssueReasonOptionContexts(getBureauCloseReasonOptionList(bureauActionsContext.getId()));
                            bureauActionsContext.setInhibitReasonList(getInhibitReasonList(bureauActionsContext.getId()));
                            dataList.add(bureauActionsContext);
                        }
                        V3Util.createRecord(modBean.getModule(FlaggedEventBureauActionModule.MODULE_NAME), dataList);
                        context.put(RemoteMonitorConstants.FLAGGED_EVENT_BUREAU_ACTIONS, dataList);
                    }
                }
            }
        }
        return false;
    }

    private static List<FlaggedEventRuleBureauEvaluationContext> getBureauEvaluation(Long id) throws Exception {
        if (id != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE", "flaggedEventRule", String.valueOf(id), NumberOperators.EQUALS));
            List<SupplementRecord> supplementRecords = Arrays.asList((SupplementRecord) modBean.getField("troubleShootingTips",FlaggedEventBureauEvaluationModule.MODULE_NAME));
            return V3RecordAPI.getRecordsListWithSupplements(FlaggedEventBureauEvaluationModule.MODULE_NAME, null, FlaggedEventRuleBureauEvaluationContext.class, criteria, supplementRecords);
        }
        return null;
    }
    private static List<BureauInhibitReasonListContext> getInhibitReasonList(Long id) throws Exception {
        if (id != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_BUREAU_EVALUATION", "bureauEvaluationId", String.valueOf(id), NumberOperators.EQUALS));
            return V3RecordAPI.getRecordsListWithSupplements(BureauInhibitReasonListModule.MODULE_NAME, null, BureauInhibitReasonListContext.class, criteria, null);
        }
        return null;
    }

    private static List<BureauCauseListContext> getBureauCauseList(Long id) throws Exception {
        if (id != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", String.valueOf(id), NumberOperators.EQUALS));
            return V3RecordAPI.getRecordsListWithSupplements(BureauCauseListModule.MODULE_NAME, null, BureauCauseListContext.class, criteria, null);
        }
        return null;
    }

    private static List<BureauResolutionListContext> getBureauResolutionList(Long id) throws Exception {
        if (id != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", String.valueOf(id), NumberOperators.EQUALS));
            return V3RecordAPI.getRecordsListWithSupplements(BureauResolutionListModule.MODULE_NAME, null, BureauResolutionListContext.class, criteria, null);
        }
        return null;
    }

    private static List<BureauCloseIssueReasonOptionContext> getBureauCloseReasonOptionList(Long id) throws Exception {
        if (id != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("BUREAU_EVALUATION_ID", "bureauEvaluationId", String.valueOf(id), NumberOperators.EQUALS));
            return V3RecordAPI.getRecordsListWithSupplements(BureauCloseIssueReasonOptionListModule.MODULE_NAME, null, BureauCloseIssueReasonOptionContext.class, criteria, null);
        }
        return null;
    }
}
