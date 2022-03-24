package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class PrepareReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newRuleCtx = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        NewReadingRuleContext oldRule = NewReadingRuleAPI.getRule(newRuleCtx.getId());
        if (oldRule == null) {
            throw new Exception("Rule (" + newRuleCtx.getId() + ") is not found!");
        }
        constructRuleDetails(newRuleCtx, oldRule);
        context.put(FacilioConstants.ContextNames.NEW_READING_RULE, oldRule);
        return false;
    }

    private void constructRuleDetails(NewReadingRuleContext newCtx, NewReadingRuleContext oldRule) {
        if (newCtx.getName() != null) {
            oldRule.setName(newCtx.getName());
        }
        if (newCtx.getDescription() != null) {
            oldRule.setDescription(newCtx.getDescription());
        }
        if (newCtx.getAssetCategoryId() != null) {
            oldRule.setAssetCategoryId(newCtx.getAssetCategoryId());
        }
        if (newCtx.getAppliedTo() != null) {
            oldRule.setAppliedTo(newCtx.getAppliedTo());
        }
        if (newCtx.getAssets() != null) {
            oldRule.setAssets(newCtx.getAssets());
        }
        if (newCtx.getAlarmDetails() != null) {
            oldRule.setAlarmDetails(newCtx.getAlarmDetails());
        }
        if (newCtx.getRcaRules() != null) {
            oldRule.setRcaRules(newCtx.getRcaRules());
        }
        if (newCtx.getCondition() != null) {
            oldRule.setCondition(newCtx.getCondition());
        }
    }

}
