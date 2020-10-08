package com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateNewsSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewsAndInformationContext> newsList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(newsList)) {
            for(NewsAndInformationContext news : newsList){
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(news.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(news.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(news.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(news.getAudience());
//                news.setAudience(news.getAudience());

                if(news.getAudience() != null && news.getAudience().getId() > 0){
                    continue;
                }
                if(news.getAudience() != null && CollectionUtils.isNotEmpty(news.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(news.getAudience());
                    news.setAudience(news.getAudience());
                }
            }
        }
        return false;
    }
}
