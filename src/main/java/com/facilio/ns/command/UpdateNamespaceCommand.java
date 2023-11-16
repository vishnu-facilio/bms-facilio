package com.facilio.ns.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


public class UpdateNamespaceCommand extends FacilioCommand {

    NameSpaceContext ns;
    ResourceType resourceType;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        IConnectedRule connectedRule = (IConnectedRule) context.get(moduleName);
        if (connectedRule.getNs() != null) {
            this.ns = connectedRule.getNs();
        }
        this.resourceType = connectedRule.getCategory().getResType();
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
