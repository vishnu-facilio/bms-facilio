package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.NonNull;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadingRuleDependenciesCommand extends FacilioCommand {

    NewReadingRuleContext rule;

    @Override
    public boolean executeCommand(Context ctx) throws Exception {

        this.rule = (NewReadingRuleContext) ctx.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        this.rule.getWorkflowContext().setIsV2Script(true);
        this.rule.getNs().setType(NSType.READING_RULE.getIndex());

        ctx.put(FacilioConstants.ContextNames.READING_NAME, rule.getName());
        ctx.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, NewReadingRuleAPI.READING_RULE_FIELD_TABLE_NAME);
        FacilioField ruleField = FieldFactory.getField(null, rule.getName(), null, null, FieldType.BOOLEAN);
        FacilioField addtnMsgField = FieldFactory.getField(null, "INFO", null, null, FieldType.BIG_STRING);
        ruleField.setDefault(false);
        addtnMsgField.setDefault(false);
        
        ArrayList<FacilioField> fieldList = new ArrayList<FacilioField>();
        
        fieldList.add(ruleField);
        fieldList.add(addtnMsgField);

        if(rule.getImpact() != null) {
        	
        	FacilioField energyImpactField = FieldFactory.getField("energyImpact", "Energy Impact", "ENERGY_IMPACT", null, FieldType.DECIMAL);
            FacilioField costImpactField = FieldFactory.getField("costImpact", "Cost Impact", "COST_IMPACT", null, FieldType.DECIMAL);
            
            fieldList.add(energyImpactField);
            fieldList.add(costImpactField);
        }

        ctx.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,fieldList);
        
        ctx.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
        ctx.put(WorkflowV2Util.WORKFLOW_CONTEXT, rule.getWorkflowContext());

        setReadingParent(ctx);
        setAssets();

        return false;
    }

    private void setAssets() throws Exception {
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

    private void setReadingParent(Context context) {

        switch (rule.getResourceTypeEnum()) {
            case ASSET_CATEGORY:
                context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
                context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, rule.getAssetCategoryId());
                context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
                break;
            case SPACE_CATEGORY:
                throw new RuntimeException("Not implemented!!");
        }
    }
}
