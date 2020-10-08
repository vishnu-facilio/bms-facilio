package com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

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
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(contact.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(contact.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(contact.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(contact.getAudience());
//                contact.setAudience(contact.getAudience());

                if(contact.getAudience() != null && contact.getAudience().getId() > 0){
                    continue;
                }
                if(contact.getAudience() != null && CollectionUtils.isNotEmpty(contact.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(contact.getAudience());
                    contact.setAudience(contact.getAudience());
                }
            }
        }
        return false;
    }
}
