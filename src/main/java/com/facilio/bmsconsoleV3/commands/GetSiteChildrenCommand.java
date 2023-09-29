package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetSiteChildrenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long siteId = (long) context.get(FacilioConstants.ContextNames.SITE_ID);
        List<BaseSpaceContext> children = SpaceAPI.getSiteChildren(siteId);
        return false;
    }

}
