package com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectorySharingContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillContactDirectorySharingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                ContactDirectoryContext record = (ContactDirectoryContext) CommandUtil.getModuleData(context, moduleName,recId);
                if (record != null) {
                    if(record.getAudience() != null){
                        CommunityFeaturesAPI.setAudienceSharingInfo(record.getAudience());
                    }
                    else {
                        List<CommunitySharingInfoContext> list = (List<CommunitySharingInfoContext>) CommunityFeaturesAPI.getSharingInfo(record, "contactdirectorysharing", "contactDirectory");
                        if (CollectionUtils.isNotEmpty(list)) {
                            record.setContactdirectorysharing(list);
                        }
                    }
                }
            }
        }

        return false;
    }
}