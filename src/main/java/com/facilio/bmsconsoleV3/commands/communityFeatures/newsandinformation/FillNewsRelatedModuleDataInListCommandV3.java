package com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.NewsAndInformationContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.NewsAndInformationSharingContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
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
                news.setNewsandinformationsharing((List<NewsAndInformationSharingContext>)  AnnouncementAPI.getSharingInfo(news, FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_SHARING, "newsAndInformation"));
            }

        }

        return false;
    }
}
