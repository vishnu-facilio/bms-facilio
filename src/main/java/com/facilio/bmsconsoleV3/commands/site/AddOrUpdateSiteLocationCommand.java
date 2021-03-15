package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddOrUpdateSiteLocationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(moduleName);
        for (ModuleBaseWithCustomFields record: moduleBaseWithCustomFields) {
            var siteContext = (V3SiteContext) record;

            LocationContext location = siteContext.getLocation();
            if (location != null && location.getLat() != -1 && location.getLng() != -1) {
                location.setName(siteContext.getName()+"_Location");

                Context locationContext = new FacilioContext();
                Constants.setRecord(locationContext, location);

                if (location.getId() > 0) {
                    context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
                    FacilioChain updateLocation = FacilioChainFactory.updateLocationChain();
                    updateLocation.execute(locationContext);
                } else {
                    FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                    addLocation.execute(locationContext);

                    long recordId = Constants.getRecordId(locationContext);
                    location.setId(recordId);
                }
            } else {
                siteContext.setLocation(null);
            }
        }
        return false;
    }
}
