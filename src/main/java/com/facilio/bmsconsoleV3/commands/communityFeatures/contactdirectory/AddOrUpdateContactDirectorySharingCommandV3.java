package com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
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
                if(contact.getAudience() != null && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(contact.getAudience() == null && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(contact.getAudience() != null && contact.getAudience().getId() > 0){
                    continue;
                }
                else if(contact.getAudience() != null && CollectionUtils.isNotEmpty(contact.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(contact.getAudience());
                    contact.setAudience(contact.getAudience());
                }
            }
        }
        return false;
    }
}
