package com.facilio.connected.scopeHandler;

import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
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

    Map<Long, String> getChildTypes(Set<Long> ids) throws Exception;

    Map<String, FacilioField> getReadings(Long id, Long parentId) throws Exception;

    Pair<Long, Long> getParentIdAndCategoryId(V3Context parent);

    FacilioField getTypeField() throws Exception;
}
