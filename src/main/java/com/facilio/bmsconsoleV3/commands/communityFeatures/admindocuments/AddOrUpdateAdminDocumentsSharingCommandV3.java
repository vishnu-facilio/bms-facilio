package com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.communityfeatures.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
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

public class AddOrUpdateAdminDocumentsSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AdminDocumentsContext> docs = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(docs)) {
            for(AdminDocumentsContext doc : docs){
                if(AccountUtil.getCurrentSiteId() != -1) {
                    doc.setSiteId(AccountUtil.getCurrentSiteId());
                }
                Map<String, List<Map<String, Object>>> subforms = doc.getSubForm();
                if(CollectionUtils.isEmpty(doc.getAudience()) && MapUtils.isNotEmpty(subforms) && subforms.containsKey(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS_SHARING)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Sharing Information. Can be  either audience or list of sharing info'");
                }
                else if(CollectionUtils.isEmpty(doc.getAudience()) && (MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS_SHARING))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing Information cannot be empty");
                }
                else if(CollectionUtils.isNotEmpty(doc.getAudience())){
                    continue;
                }
                else if(CollectionUtils.isNotEmpty(doc.getAudience())){
                    for(AudienceContext audience : doc.getAudience()){
                        if(CollectionUtils.isNotEmpty(audience.getAudienceSharing())){
                            CommunityFeaturesAPI.addAudience(audience);
                        }
                    }
                    doc.setAudience(doc.getAudience());
                }
            }
        }
        return false;
    }
}
