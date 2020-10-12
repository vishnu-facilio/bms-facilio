package com.facilio.bmsconsoleV3.commands.communityFeatures.newsandinformation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.NewsAndInformationContext;
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

public class AddOrUpdateNewsSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewsAndInformationContext> newsList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(newsList)) {
            for(NewsAndInformationContext news : newsList){
                if(AccountUtil.getCurrentSiteId() != -1) {
                    news.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = news.getSubForm();
                if(news.getAudience() != null && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(news.getAudience() == null && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(news.getAudience() != null && news.getAudience().getId() > 0){
                    continue;
                }
                else if(news.getAudience() != null && CollectionUtils.isNotEmpty(news.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(news.getAudience());
                    news.setAudience(news.getAudience());
                }
            }
        }
        return false;
    }
}
