package com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateAdminDocumentsSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AdminDocumentsContext> docs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(docs)) {
            for(AdminDocumentsContext doc : docs){
                //uncomment the commented snippet & remove the existing uncommented after supporting audience in client

//                if(doc.getAudience() == null){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Audience Information cannot be empty");
//                }
//                if(doc.getAudience().getId() > 0){
//                    continue;
//                }
//                if(CollectionUtils.isEmpty(doc.getAudience().getAudienceSharing())){
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Audience Information");
//                }
//                CommunityFeaturesAPI.addAudience(doc.getAudience());
//                doc.setAudience(doc.getAudience());

                if(doc.getAudience() != null && doc.getAudience().getId() > 0){
                    continue;
                }
                if(doc.getAudience() != null && CollectionUtils.isNotEmpty(doc.getAudience().getAudienceSharing())){
                    CommunityFeaturesAPI.addAudience(doc.getAudience());
                    doc.setAudience(doc.getAudience());
                }
            }
        }
        return false;
    }
}
