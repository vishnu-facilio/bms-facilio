package com.facilio.connected.scopeHandler;

import com.facilio.bmsconsole.util.CommissioningApi;

import com.facilio.constants.FacilioConstants;

import java.util.Map;

import java.util.Set;

public class SpaceCommissioningHandler implements ScopeCommissioningHandler {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SPACE;
    }

    @Override
    public String getSubModuleName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getDisplayName() {
        return "Space";
    }

    @Override
    public String getTypeDisplayName() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void updateConnectionStatus(Set<Long> assetIds, boolean isConnected) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getResourceName(Long resourceId) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<Long, String> getParent(Set<Long> assetIds) throws Exception {
        return CommissioningApi.getParent(assetIds,FacilioConstants.ContextNames.RESOURCE);
    }

    @Override
    public Map<Long,String> getChildTypes(Set<Long>categoryIds) throws Exception{
        throw new RuntimeException("Not implemented");
    }
}