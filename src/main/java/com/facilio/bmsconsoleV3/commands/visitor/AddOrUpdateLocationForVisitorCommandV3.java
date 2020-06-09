package com.facilio.bmsconsoleV3.commands.visitor;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateLocationForVisitorCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorContext> visitors = recordMap.get(moduleName);
        if(!CollectionUtils.isEmpty(visitors)) {
                for(V3VisitorContext v : visitors) {
                    //update location
                    LocationContext location = v.getLocation();

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
                            v.setLocation(location);
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
                        v.setLocation(null);
                    }
                }
        }
        return false;
    }
}
