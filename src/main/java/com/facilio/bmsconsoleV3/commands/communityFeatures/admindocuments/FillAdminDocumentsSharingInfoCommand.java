package com.facilio.bmsconsoleV3.commands.communityFeatures.admindocuments;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.tenantEngagement.AdminDocumentsContext;
import com.facilio.bmsconsoleV3.context.tenantEngagement.AdminDocumentsSharingContext;
import com.facilio.bmsconsoleV3.util.AnnouncementAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FillAdminDocumentsSharingInfoCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);
        String moduleName = Constants.getModuleName(context);
        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long recId : recordIds) {
                AdminDocumentsContext record = (AdminDocumentsContext) CommandUtil.getModuleData(context, moduleName,recId);
                if (record != null) {
                    List<AdminDocumentsSharingContext> list = (List<AdminDocumentsSharingContext>) AnnouncementAPI.getSharingInfo(record, "admindocumentsharing", "adminDocument");
                    if (CollectionUtils.isNotEmpty(list)) {
                        record.setAdmindocumentsharing(list);
                    }
                }
            }
        }

        return false;
    }
}