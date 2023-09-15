package com.facilio.ns.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateReadingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateNamespaceCommand extends FacilioCommand {

    NameSpaceContext ns;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(moduleName.equals(FacilioConstants.ReadingRules.NEW_READING_RULE)){
            NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
            this.ns = rule.getNs();
        }
        else if(moduleName.equals(FacilioConstants.ReadingKpi.READING_KPI)){
            ReadingKPIContext kpi = (ReadingKPIContext) context.get(FacilioConstants.ReadingKpi.READING_KPI);
            this.ns = kpi.getNs();
        }
        else if(moduleName.equals(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING)){
            VirtualMeterTemplateReadingContext vmt = (VirtualMeterTemplateReadingContext) context.get(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING);
            this.ns = vmt.getNs();
        }
        updateIncludedAssets();
        updateNamespace();
        return false;
    }
    private void updateIncludedAssets() throws Exception {
        if (ns != null) {
            List<Long> includedAssets = ns.getIncludedAssetIds();
            if (CollectionUtils.isNotEmpty(includedAssets)) {
                List<ResourceContext> filteredAssets = ResourceAPI.getResources(includedAssets, false);
                List<Long> filteredAssetIds = filteredAssets.stream().map(ResourceContext::getId).collect(Collectors.toList());
                ns.setIncludedAssetIds(filteredAssetIds);
            }
        }
    }
    private void updateNamespace() throws Exception {
        if (ns != null) {
            Constants.getNsBean().updateNamespace(ns);
        }
    }
}
