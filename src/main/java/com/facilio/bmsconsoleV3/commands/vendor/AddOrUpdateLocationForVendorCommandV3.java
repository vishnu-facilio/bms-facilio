package com.facilio.bmsconsoleV3.commands.vendor;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateLocationForVendorCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VendorContext> vendors = recordMap.get(moduleName);
        if(!CollectionUtils.isEmpty(vendors)) {
            for(V3VendorContext v : vendors) {
                //set vendor source if doesnt have
                if(v.getRegisteredBy() != null && v.getRegisteredBy().getId() > 0) {
                    v.setVendorSource(1);
                        V3TenantContext tenant = V3PeopleAPI.getTenantForUser(v.getRegisteredBy().getId());
                        if(tenant != null && tenant.getId() > 0) {
                            v.setTenant(tenant);
                        }

                }
                //update location
                LocationContext location = v.getAddress();

                if(location != null && location.getLat() != -1 && location.getLng() != -1)
                {
                    FacilioChain locationChain = null;
                    location.setName(v.getName()+"_Location");

                    if (location.getId() > 0) {
                        locationChain = FacilioChainFactory.updateLocationChain();
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));

                        locationChain.execute();
                        v.setAddress(location);
                    }
                    else {
                        locationChain = FacilioChainFactory.addLocationChain();
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
                        locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));

                        locationChain.execute();
                        long locationId = (long) locationChain.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
                        location.setId(locationId);
                    }
                }
                else {
                    v.setAddress(null);
                }
            }
        }
        return false;
    }
}
