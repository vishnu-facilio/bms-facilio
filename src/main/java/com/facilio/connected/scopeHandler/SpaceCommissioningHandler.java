package com.facilio.connected.scopeHandler;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.util.CommissioningApi;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.tuple.Pair;

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

    @Override
    public Map<String, FacilioField> getReadings(Long id, Long parentId, AgentConstants.AutoMappingReadingFieldName autoMappingReadingFieldNameEnum) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Pair<Long, Long> getParentIdAndCategoryId(V3Context v3Context) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public FacilioField getTypeField() {
        throw new RuntimeException("Not implemented");
    }

}