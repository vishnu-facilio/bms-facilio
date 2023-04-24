package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GetCommentSharingOptionsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<CommentSharingContext> defaultSharingApps = NotesAPI.fetchDefaultCommentSharingApps();
        ArrayList<ApplicationContext> availablePortalApps = new ArrayList<>(ApplicationApi.getLicensedPortalApps());
        Map<String,ArrayList> resultSet = new HashMap<>();
        resultSet.put(FacilioConstants.ContextNames.COMMENT_SHARING_PREFERENCES,defaultSharingApps);
        resultSet.put(FacilioConstants.ContextNames.LICENSED_PORTAL_APPS,availablePortalApps);
        context.put(FacilioConstants.ContextNames.COMMENT_SHARING_OPTIONS,resultSet);
        return false;
    }


}
