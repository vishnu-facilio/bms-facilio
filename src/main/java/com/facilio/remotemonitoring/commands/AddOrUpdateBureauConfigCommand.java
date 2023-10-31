package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauEvaluationModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
                if (CollectionUtils.isNotEmpty(bureauConfigs)) {
                    if (bureauConfigs.size() > 3) {
                        throw new IllegalArgumentException("Maximum 3 evaluation teams can be configured for a flagged event");
                    }
                    int order = 0;
                    int size = bureauConfigs.size();
                    for (FlaggedEventRuleBureauEvaluationContext bureauConfig : bureauConfigs) {
                        ++order;
                        flaggedEventRule.setFlaggedEventRuleBureauEvaluationContexts(null);
                        bureauConfig.setFlaggedEventRule(flaggedEventRule);
                        bureauConfig.setIsFinalTeam(size == order);
                        bureauConfig.setOrder(order);
                        if (bureauConfig.getEmailRule() != null && bureauConfig.getEmailRule().getActions() != null) {
                            Criteria criteria = new Criteria();
                            FacilioField bureauDetailField = modBean.getField("currentBureauActionDetail", FlaggedEventModule.MODULE_NAME);
                            Criteria lookupCriteria = new Criteria();
                            lookupCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("order", FlaggedEventBureauEvaluationModule.MODULE_NAME), String.valueOf(order), NumberOperators.EQUALS));
                            criteria.addAndCondition(CriteriaAPI.getCondition(bureauDetailField, lookupCriteria, LookupOperator.LOOKUP));
                            criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME, FlaggedEventModule.MODULE_NAME), String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));

                            FieldChangeFieldContext changeFieldContext = new FieldChangeFieldContext();
                            changeFieldContext.setFieldId(bureauDetailField.getFieldId());
                            bureauConfig.setEmailRuleId(FlaggedEventUtil.addFieldChangeEmailRule(bureauConfig.getEmailRule(), "Email Notification Rule for Flagged Event Bureau - " + flaggedEventRule.getId(), Arrays.asList(changeFieldContext), criteria));
                        }
                        V3Util.createRecord(modBean.getModule(FlaggedEventBureauEvaluationModule.MODULE_NAME), Arrays.asList(bureauConfig));
                    }
                }
            }
        }
        return false;
    }
}