package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

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
                if(AccountUtil.getCurrentSiteId() != -1) {
                    announcement.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = announcement.getSubForm();
                if(CollectionUtils.isEmpty(announcement.getAudience()) && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(CollectionUtils.isEmpty(announcement.getAudience()) && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENTS_SHARING_INFO))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(CollectionUtils.isNotEmpty(announcement.getAudience())){
                    continue;
                }
                else if(CollectionUtils.isNotEmpty(announcement.getAudience())){
                    for(AudienceContext audience : announcement.getAudience()){
                        if(CollectionUtils.isNotEmpty(audience.getAudienceSharing())){
                            CommunityFeaturesAPI.addAudience(audience);
                        }
                    }
                    announcement.setAudience(announcement.getAudience());
                }
            }
        }

            return false;
    }


}
