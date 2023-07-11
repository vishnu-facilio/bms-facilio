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

public class AddOrUpdateBureauInhibitReasonListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleBureauEvaluationContext> flaggedEventRuleBureauEvaluationList = (List<FlaggedEventRuleBureauEvaluationContext>) recordMap.get(FlaggedEventBureauEvaluationModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(flaggedEventRuleBureauEvaluationList)) {
            for (FlaggedEventRuleBureauEvaluationContext flaggedEventRuleBureauEvaluation : flaggedEventRuleBureauEvaluationList) {
                List<BureauInhibitReasonListContext> inhibitReasonList = flaggedEventRuleBureauEvaluation.getInhibitReasonList();
                if(CollectionUtils.isNotEmpty(inhibitReasonList)) {
                    List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
                    for(BureauInhibitReasonListContext inhibitReason : inhibitReasonList) {
                        flaggedEventRuleBureauEvaluation.setInhibitReasonList(null);
                        inhibitReason.setFlaggedEventBureauEvaluation(flaggedEventRuleBureauEvaluation);
                        dataList.add(inhibitReason);
                    }
                    V3Util.createRecord(modBean.getModule(BureauInhibitReasonListModule.MODULE_NAME),dataList);
                }
                addBureauResolutionList(flaggedEventRuleBureauEvaluation);
                addBureauCauseList(flaggedEventRuleBureauEvaluation);
                addCloseIssueReasonListOption(flaggedEventRuleBureauEvaluation);
            }
        }
        return false;
    }
    private static void addBureauResolutionList(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauResolutionListContext> list = rule.getResolutionList();
        if(CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for(BureauResolutionListContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauResolutionListModule.MODULE_NAME),dataList);
        }
    }
    private static void addBureauCauseList(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauCauseListContext> list = rule.getCauseList();
        if(CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for(BureauCauseListContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauCauseListModule.MODULE_NAME),dataList);
        }
    }
    private static void addCloseIssueReasonListOption(FlaggedEventRuleBureauEvaluationContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<BureauCloseIssueReasonOptionContext> list = rule.getCloseIssueReasonOptionContexts();
        if(CollectionUtils.isNotEmpty(list)) {
            List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
            for(BureauCloseIssueReasonOptionContext data : list) {
                data.setBureauEvaluationId(rule.getId());
                dataList.add(data);
            }
            V3Util.createRecord(modBean.getModule(BureauCloseIssueReasonOptionListModule.MODULE_NAME),dataList);
        }
    }
}
