package com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NeighbourhoodContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateNeighbourhoodSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NeighbourhoodContext> neighbourhoods = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(neighbourhoods)) {
            for(NeighbourhoodContext neighbourhood : neighbourhoods){
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(neighbourhood.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(neighbourhood.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(neighbourhood.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(neighbourhood.getAudience());
//                neighbourhood.setAudience(neighbourhood.getAudience());

                if(neighbourhood.getAudience() != null && neighbourhood.getAudience().getId() > 0){
                    continue;
                }
                if(neighbourhood.getAudience() != null && CollectionUtils.isNotEmpty(neighbourhood.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(neighbourhood.getAudience());
                    neighbourhood.setAudience(neighbourhood.getAudience());
                }
            }
        }
        return false;
    }
}
