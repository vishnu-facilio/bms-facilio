package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.NonNull;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrepareReadingRuleForUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newRuleCtx = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        newRuleCtx.getNs().getWorkflowContext().setIsV2Script(Boolean.TRUE);
        NewReadingRuleContext oldRule = NewReadingRuleAPI.getRule(newRuleCtx.getId());
        if (oldRule == null) {
            throw new Exception("Rule (" + newRuleCtx.getId() + ") is not found!");
        }
        constructRuleDetails(context, newRuleCtx, oldRule);
        setAssets(oldRule);
        context.put(FacilioConstants.ContextNames.NEW_READING_RULE, oldRule);
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, newRuleCtx.getNs().getWorkflowContext());

        return false;
    }

    private void constructRuleDetails(Context ctx, NewReadingRuleContext newCtx, NewReadingRuleContext oldRule) {
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
        if (newCtx.getAlarmRCARules() != null) {
            oldRule.setAlarmRCARules(newCtx.getAlarmRCARules());
        }
        if (newCtx.getNs() != null && newCtx.getNs().getWorkflowContext()!=null) {
            newCtx.getNs().setId(oldRule.getNs().getId());
            newCtx.getNs().getWorkflowContext().setId(oldRule.getNs().getWorkflowId());
            newCtx.getNs().setWorkflowId(newCtx.getNs().getWorkflowContext().getId());
            oldRule.setNs(newCtx.getNs());
        }

        if (newCtx.getImpact() != null) {
            oldRule.setImpact(newCtx.getImpact());
            if (newCtx.getImpact().getId() == -1) {
                ctx.put("canDeleteFaultImpact", true);
            }
        }
    }

    private void setAssets(NewReadingRuleContext rule) throws Exception {
        List<Long> assets = rule.getAssets();
        if (CollectionUtils.isNotEmpty(assets)) {
            @NonNull List<AssetContext> assetInfo = AssetsAPI.getAssetInfo(assets);

            for (Long asset : assets) {
                boolean present = assetInfo.stream().filter(assetCtx -> assetCtx.getId() == asset).findAny().isPresent();
                if (!present) {
                    throw new RuntimeException("Asset (" + asset + ") is not found");
                }
            }

            Map<Long, ResourceContext> resourcesMap = assetInfo.stream().collect(Collectors.toMap(ResourceContext::getId, Function.identity()));
            rule.setMatchedResources(resourcesMap);
        }
    }

}
