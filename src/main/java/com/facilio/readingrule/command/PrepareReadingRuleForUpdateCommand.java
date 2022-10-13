package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.NonNull;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrepareReadingRuleForUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        NewReadingRuleContext readingRule = (NewReadingRuleContext) recordMap.get(moduleName).get(0);
        NameSpaceContext nameSpaceContext = NamespaceAPI.getNameSpaceByRuleId(readingRule.getId(), NSType.READING_RULE);
        WorkflowContext workflow = readingRule.getNs().getWorkflowContext();
        workflow.setId(nameSpaceContext.getWorkflowId());
        NewReadingRuleContext oldRule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(readingRule.getId())).get(0);
        if (oldRule == null) {
            throw new Exception("Rule (" + readingRule.getId() + ") is not found!");
        }
        constructRuleDetails(context, readingRule, oldRule);
        setAssets(oldRule);
        context.put(FacilioConstants.ContextNames.NEW_READING_RULE, oldRule);
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);

        return false;
    }

    private void constructRuleDetails(Context ctx, NewReadingRuleContext newCtx, NewReadingRuleContext oldRule) {
        if (newCtx.getAssetCategory() != null) {
            oldRule.setAssetCategory(newCtx.getAssetCategory());
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
        if (newCtx.getNs() != null && newCtx.getNs().getWorkflowContext() != null) {
            newCtx.getNs().setId(oldRule.getNs().getId());
            newCtx.getNs().getWorkflowContext().setId(oldRule.getNs().getWorkflowId());
            newCtx.getNs().setWorkflowId(newCtx.getNs().getWorkflowContext().getId());
            oldRule.setNs(newCtx.getNs());
        }

        if (newCtx.getImpact() != null) {
            if (oldRule.getImpact() != null) {
                if (oldRule.getImpact().getId() != newCtx.getImpact().getId()) {
                    ctx.put("canDeleteFaultImpact", true);
                }
                if (oldRule.getImpact().getId() == newCtx.getImpact().getId()) {
                    ctx.put("addFaultImpact", false);
                }
            }
            oldRule.setImpact(newCtx.getImpact());
            if (newCtx.getImpact().getId() == -1) {
                ctx.put("canDeleteFaultImpact", true);
            }
        }
        if (newCtx.getStatus() != oldRule.getStatus()) {
            oldRule.setStatus(newCtx.getStatus());
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
