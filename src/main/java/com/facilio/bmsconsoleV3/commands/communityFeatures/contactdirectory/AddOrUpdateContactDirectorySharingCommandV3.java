package com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.DealsAndOffersContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
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

public class AddOrUpdateContactDirectorySharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ContactDirectoryContext> contacts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(contacts)) {
            for(ContactDirectoryContext contact : contacts){
                if(AccountUtil.getCurrentSiteId() != -1) {
                    contact.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = contact.getSubForm();
                if(CollectionUtils.isEmpty(contact.getAudience()) && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(CollectionUtils.isEmpty(contact.getAudience()) && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(CollectionUtils.isNotEmpty(contact.getAudience())){
                    continue;
                }
                else if(CollectionUtils.isNotEmpty(contact.getAudience())){
                    for(AudienceContext audience : contact.getAudience()){
                        if(CollectionUtils.isNotEmpty(audience.getAudienceSharing())){
                            CommunityFeaturesAPI.addAudience(audience);
                        }
                    }
                    contact.setAudience(contact.getAudience());
                }
            }
        }
        return false;
    }
}
