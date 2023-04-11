package com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationSharingContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillNewsRelatedModuleDataInListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //this command has to be removed once the related module data is fetched in the list api in the v3 framework.
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewsAndInformationContext> newsList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(newsList)) {
            for(NewsAndInformationContext news : newsList) {
                news.setNewsandinformationnotes(NotesAPI.fetchNotes(news.getId(), FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_NOTES));
                news.setNewsandinformationattachments(AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_ATTACHMENTS, news.getId(), false));
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

        return false;
    }
}
