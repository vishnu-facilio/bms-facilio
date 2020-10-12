package com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
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

public class AddOrUpdateDealsSharingInfoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<DealsAndOffersContext> deals = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(deals)) {
            for(DealsAndOffersContext deal : deals){
                if(AccountUtil.getCurrentSiteId() != -1) {
                    deal.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = deal.getSubForm();
                if(deal.getAudience() != null && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(deal.getAudience() == null && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(deal.getAudience() != null && deal.getAudience().getId() > 0){
                    continue;
                }
                else if(deal.getAudience() != null && CollectionUtils.isNotEmpty(deal.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(deal.getAudience());
                    deal.setAudience(deal.getAudience());
                }
            }
        }
        return false;
    }
}
