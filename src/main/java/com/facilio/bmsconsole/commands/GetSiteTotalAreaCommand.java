package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;

public class GetSiteTotalAreaCommand extends FacilioCommand  {
    public boolean executeCommand(Context context) throws Exception {
        List<BaseSpaceContext> sites = CommonCommandUtil.getMySites();
        long totalArea = 0;
        for (BaseSpaceContext site: sites) {
            if (site.getArea() > 0) {
                totalArea += site.getArea();
            }
        }
        context.put(FacilioConstants.ContextNames.TOTAL_AREA, totalArea);

        return false;
    }
}
