package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateBureauActionListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventBureauActionsContext> flaggedEventRuleBureauActionList = (List<FlaggedEventBureauActionsContext>) recordMap.get(FlaggedEventBureauActionModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(flaggedEventRuleBureauActionList)) {
            for (FlaggedEventBureauActionsContext flaggedEventRuleActionEvaluation : flaggedEventRuleBureauActionList) {
                List<BureauInhibitReasonListContext> inhibitReasonList = flaggedEventRuleActionEvaluation.getInhibitReasonList();
                if (CollectionUtils.isNotEmpty(inhibitReasonList)) {
                    List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
                    for (BureauInhibitReasonListContext inhibitReason : inhibitReasonList) {
                        flaggedEventRuleActionEvaluation.setInhibitReasonList(null);
                        inhibitReason.setFlaggedEventBureauEvaluation(flaggedEventRuleActionEvaluation);
                        dataList.add(inhibitReason);
                    }
                    V3Util.createRecord(modBean.getModule(BureauInhibitReasonListModule.MODULE_NAME), dataList);
                }
                addBureauResolutionList(flaggedEventRuleActionEvaluation);
                addBureauCauseList(flaggedEventRuleActionEvaluation);
                addCloseIssueReasonListOption(flaggedEventRuleActionEvaluation);
                addInhibitReasonList(flaggedEventRuleActionEvaluation);
            }
        }
        return false;
    }

    private static void addInhibitReasonList(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauInhibitReasonListContext> list = rule.getInhibitReasonList();
        if (CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for (BureauInhibitReasonListContext data : list) {
                FlaggedEventRuleBureauEvaluationContext eval = new FlaggedEventRuleBureauEvaluationContext();
                eval.setId(rule.getId());
                data.setFlaggedEventBureauEvaluation(eval);
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauInhibitReasonListModule.MODULE_NAME), dataList);
        }
    }

    private static void addBureauResolutionList(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauResolutionListContext> list = rule.getResolutionList();
        if (CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for (BureauResolutionListContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauResolutionListModule.MODULE_NAME), dataList);
        }
    }

    private static void addBureauCauseList(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauCauseListContext> list = rule.getCauseList();
        if (CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for (BureauCauseListContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauCauseListModule.MODULE_NAME), dataList);
        }
    }

    private static void addCloseIssueReasonListOption(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauCloseIssueReasonOptionContext> list = rule.getCloseIssueReasonOptionContexts();
        if (CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for (BureauCloseIssueReasonOptionContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauCloseIssueReasonOptionListModule.MODULE_NAME), dataList);
        }
    }
}
