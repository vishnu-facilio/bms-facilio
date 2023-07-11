package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauEvaluationModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateBureauConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                List<FlaggedEventRuleBureauEvaluationContext> bureauConfigs = flaggedEventRule.getFlaggedEventRuleBureauEvaluationContexts();
                List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(bureauConfigs)) {
                    if(bureauConfigs.size() > 3) {
                        throw new IllegalArgumentException("Maximum 3 evaluation teams can be configured for a flagged event");
                    }
                    int i = 0;
                    int size = bureauConfigs.size();
                    for(FlaggedEventRuleBureauEvaluationContext bureauConfig : bureauConfigs) {
                        ++i;
                        flaggedEventRule.setFlaggedEventRuleBureauEvaluationContexts(null);
                        bureauConfig.setFlaggedEventRule(flaggedEventRule);
                        bureauConfig.setIsFinalTeam(size == i);
                        bureauConfig.setOrder(i);
                        if(bureauConfig.getEmailRule() != null) {
                            Criteria criteria = new Criteria();
                            criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("flaggedEventRule", FlaggedEventModule.MODULE_NAME),String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));
                            bureauConfig.setEmailRuleId(FlaggedEventUtil.addEmailRule(bureauConfig.getEmailRule(), "Email Notification Rule for Flagged Event Bureau - " + flaggedEventRule.getId(), criteria));
                        }
                        dataList.add(bureauConfig);
                    }
                    V3Util.createRecord(modBean.getModule(FlaggedEventBureauEvaluationModule.MODULE_NAME),dataList);
                }
            }
        }
        return false;
    }
}