package com.facilio.connected.scopeHandler;

import java.util.Map;
import java.util.Set;

public interface ScopeCommissioningHandler {


    String getModuleName();

    String getSubModuleName();

    String getDisplayName();

    String getTypeDisplayName();

    void updateConnectionStatus(Set<Long> assetIds, boolean isConnected) throws Exception;

    String getResourceName(Long resourceId) throws Exception;

    Map<Long, String> getParent(Set<Long> ids) throws Exception;

    Map<Long,String> getChildTypes(Set<Long>ids) throws Exception;
}
