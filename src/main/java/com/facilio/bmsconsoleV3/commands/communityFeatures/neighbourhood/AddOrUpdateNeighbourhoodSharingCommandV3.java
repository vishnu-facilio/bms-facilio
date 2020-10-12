package com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.NeighbourhoodContext;
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

public class AddOrUpdateNeighbourhoodSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NeighbourhoodContext> neighbourhoods = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(neighbourhoods)) {
            for(NeighbourhoodContext neighbourhood : neighbourhoods){
                if(AccountUtil.getCurrentSiteId() != -1) {
                    neighbourhood.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = neighbourhood.getSubForm();
                if(neighbourhood.getAudience() != null && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(neighbourhood.getAudience() == null && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(neighbourhood.getAudience() != null && neighbourhood.getAudience().getId() > 0){
                    continue;
                }
                else if(neighbourhood.getAudience() != null && CollectionUtils.isNotEmpty(neighbourhood.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(neighbourhood.getAudience());
                    neighbourhood.setAudience(neighbourhood.getAudience());
                }
            }
        }
        return false;
    }
}
