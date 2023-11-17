package com.facilio.connected.scopeHandler;

import com.facilio.bmsconsole.context.ResourceContext;

import com.facilio.bmsconsole.util.AssetsAPI;

import com.facilio.bmsconsole.util.CommissioningApi;

import com.facilio.bmsconsole.util.ResourceAPI;

import com.facilio.constants.FacilioConstants;

import java.util.Map;

import java.util.Set;

public class AssetCommissioningHandler implements ScopeCommissioningHandler {

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ASSET;
    }

    @Override
    public String getSubModuleName() {
        return FacilioConstants.ContextNames.ASSET_CATEGORY;
    }

    @Override
    public String getDisplayName() {
        return "Asset";
    }

    @Override
    public String getTypeDisplayName() {
        return "Category";
    }

    @Override
    public void updateConnectionStatus(Set<Long> assetIds, boolean isConnected) throws Exception {
        AssetsAPI.updateAssetConnectionStatus(assetIds,isConnected);
    }

    @Override
    public String getResourceName(Long resourceId) throws Exception {
        ResourceContext resource = ResourceAPI.getResource(resourceId);
		return resource.getName();
    }

    @Override
    public  Map<Long, String> getParent(Set<Long> assetIds) throws Exception {
        return CommissioningApi.getParent(assetIds,FacilioConstants.ContextNames.RESOURCE);
    }

    @Override
    public Map<Long,String> getChildTypes(Set<Long>categoryIds) throws Exception{
        return CommissioningApi.getParent(categoryIds,getSubModuleName());
    }

}
