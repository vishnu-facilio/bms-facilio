package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;

public class AddOrUpdateAnnouncementSharingInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AnnouncementContext> announcements = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(announcements)) {
            for(AnnouncementContext announcement : announcements){
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(announcement.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(announcement.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(announcement.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(announcement.getAudience());
//                announcement.setAudience(announcement.getAudience());

                if(announcement.getAudience() != null && announcement.getAudience().getId() > 0){
                    continue;
                }
                if(announcement.getAudience() != null && CollectionUtils.isNotEmpty(announcement.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(announcement.getAudience());
                    announcement.setAudience(announcement.getAudience());
                }
            }
        }

            return false;
    }


}
