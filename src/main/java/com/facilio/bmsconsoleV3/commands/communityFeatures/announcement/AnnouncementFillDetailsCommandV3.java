package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationSharingContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AnnouncementFillDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                AnnouncementContext announcement = (AnnouncementContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT, recId);
                if (announcement != null) {
                    List<CommunitySharingInfoContext> list = null;
                    if(announcement.getAudience() != null){
                       list = CommunityFeaturesAPI.setAudienceSharingInfo(announcement.getAudience());
                    }
                    else {
                        list = (List<CommunitySharingInfoContext>) CommunityFeaturesAPI.getSharingInfo(announcement, FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO, "announcement");
                    }
                    if (CollectionUtils.isNotEmpty(list)) {
                        announcement.setAnnouncementsharing(list);
                    }
                }
            }
        }
        return false;
    }
}
