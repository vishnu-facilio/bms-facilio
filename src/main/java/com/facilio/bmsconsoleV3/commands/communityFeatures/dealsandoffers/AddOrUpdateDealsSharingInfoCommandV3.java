package com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateDealsSharingInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<DealsAndOffersContext> deals = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(deals)) {
            for(DealsAndOffersContext deal : deals){
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(deal.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(deal.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(deal.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(deal.getAudience());
//                deal.setAudience(deal.getAudience());

                if(deal.getAudience() != null && deal.getAudience().getId() > 0){
                    continue;
                }
                if(deal.getAudience() != null && CollectionUtils.isNotEmpty(deal.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(deal.getAudience());
                    deal.setAudience(deal.getAudience());
                }
            }
        }
        return false;
    }
}
