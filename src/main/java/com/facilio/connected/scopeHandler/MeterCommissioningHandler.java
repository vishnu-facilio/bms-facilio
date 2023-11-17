package com.facilio.connected.scopeHandler;


import com.facilio.bmsconsole.util.CommissioningApi;

import com.facilio.bmsconsole.util.MetersAPI;

import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.constants.FacilioConstants;

import java.util.Map;
import java.util.Set;

public class MeterCommissioningHandler implements ScopeCommissioningHandler {
    @Override
    public String getModuleName() {
        return FacilioConstants.Meter.METER;
    }

    @Override
    public String getSubModuleName() {
        return FacilioConstants.Meter.UTILITY_TYPE;
    }

    @Override
    public String getDisplayName() {
        return "Meter";
    }

    @Override
    public String getTypeDisplayName() {
        return "Utility";
    }

    @Override
    public void updateConnectionStatus(Set<Long> meterIds, boolean isCommissioned) throws Exception {
        MetersAPI.updateMeterConnectionStatus(meterIds,isCommissioned);
    }

    @Override
    public String getResourceName(Long meterId) throws Exception {
        V3MeterContext meter = MetersAPI.getMeter(meterId);
        return meter.getName();
    }

    @Override
    public Map<Long, String> getParent(Set<Long> meterIds) throws Exception {
        return CommissioningApi.getParent(meterIds,getModuleName());
    }

    @Override
    public Map<Long,String> getChildTypes(Set<Long>utilityIds) throws Exception{
        return CommissioningApi.getParent(utilityIds,getSubModuleName());
    }
}
