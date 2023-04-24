package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateCommentPreferencesChain extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<CommentSharingContext> sharingApps = (List<CommentSharingContext>) context.get(FacilioConstants.ContextNames.COMMENT_SHARING_PREFERENCES);
        if(isValidApplication(sharingApps)){
            NotesAPI.deleteDefaultCommentSharingApps();
            NotesAPI.AddDefaultCommentSharingApps(sharingApps);
        }
        return false;
    }

    private static Boolean isValidApplication(List<CommentSharingContext> sharingApps) throws Exception {
        List<ApplicationContext> availablePortalApps = ApplicationApi.getLicensedPortalApps();
        List<Long> appIds = availablePortalApps.stream().map(a -> a.getId()).collect(Collectors.toList());
        for (CommentSharingContext preference: sharingApps) {
            if(!appIds.contains(preference.getAppId())){
                return false;
            }
        }
        return true;
    }
}
