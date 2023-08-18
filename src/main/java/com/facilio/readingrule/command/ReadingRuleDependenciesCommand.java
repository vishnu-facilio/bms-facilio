package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadingRuleDependenciesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context ctx) throws Exception {
        String moduleName = Constants.getModuleName(ctx);
        Map<String, List> recordMap = (Map<String, List>) ctx.get(Constants.RECORD_MAP);
        List<NewReadingRuleContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (NewReadingRuleContext readingRule : list) {
                readingRule.getNs().getWorkflowContext().setIsV2Script(Boolean.TRUE);
                readingRule.getNs().setType(NSType.READING_RULE.getIndex());
                readingRule.getNs().setStatus(true);

                WorkflowContext workflow=readingRule.getNs().getWorkflowContext();

                if(workflow==null){
                    throw new Exception("WorkFlow can not be null for reading rule");
                }
                ctx.put(NamespaceConstants.NAMESPACE, readingRule.getNs());
                ctx.put(FacilioConstants.ContextNames.NEW_READING_RULE, readingRule);

                ctx.put(WorkflowV2Util.WORKFLOW_CONTEXT, readingRule.getNs().getWorkflowContext());
                ctx.put(NamespaceConstants.NAMESPACE_FIELDS, readingRule.getNs().getFields());
                setReadingParent(ctx, readingRule);
                setAssets(readingRule);
                setFaultImpactObject(readingRule);
            }
        }
        return false;
    }

    private void setFaultImpactObject(NewReadingRuleContext rule) throws Exception {
        Long faultImpactId = rule.getImpactId();
        if (faultImpactId != null && faultImpactId > 0) {
            FaultImpactContext faultImpactContext = FaultImpactAPI.getFaultImpactContext(faultImpactId);
            rule.setImpact(faultImpactContext);
        }
    }

    private void setAssets(NewReadingRuleContext rule) throws Exception {
        List<Long> assets = rule.getAssets();
        if (CollectionUtils.isNotEmpty(assets)) {
            List<AssetContext> assetInfo = AssetsAPI.getAssetInfo(assets);

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

    private void setReadingParent(Context context, NewReadingRuleContext rule) {

        switch (rule.getResourceTypeEnum()) {
            case SPACE_CATEGORY:
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.SPACE_CATEGORY);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, rule.getSpaceCategory().getId());
                context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
                break;
            case ASSET_CATEGORY:
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, rule.getAssetCategory().getId());
                context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
        }
    }
}
