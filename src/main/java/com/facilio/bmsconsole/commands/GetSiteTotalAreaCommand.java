package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetSiteTotalAreaCommand extends FacilioCommand  {
    public boolean executeCommand(Context context) throws Exception {
        List<SiteContext> sites = SpaceAPI.getAllSites();
        long totalArea = 0;
        for (SiteContext site: sites) {
            if (site.getArea() > 0) {
                totalArea += site.getArea();
            }
        }
        context.put(FacilioConstants.ContextNames.TOTAL_AREA, totalArea);

        return false;
    }
}
