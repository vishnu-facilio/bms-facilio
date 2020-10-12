package com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationSharingContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillNewsAndInformationDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                NewsAndInformationContext news = (NewsAndInformationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION, recId);
                if (news != null) {
                    List<CommunitySharingInfoContext> list = null;
                    if(news.getAudience() != null){
                        list = CommunityFeaturesAPI.setAudienceSharingInfo(news.getAudience());
                    }
                    else {
                        list = (List<CommunitySharingInfoContext>) CommunityFeaturesAPI.getSharingInfo(news, FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_SHARING, "newsAndInformation");
                    }
                    if (CollectionUtils.isNotEmpty(list)) {
                        news.setNewsandinformationsharing(list);
                    }
                }
            }
        }
        return false;
    }
}
