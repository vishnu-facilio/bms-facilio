package com.facilio.bmsconsoleV3.commands.imap;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.CustomMailMessageApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateLatestMessageUIDCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        SupportEmailContext updateSupportEmail = (SupportEmailContext) context.getOrDefault(FacilioConstants.ContextNames.SUPPORT_EMAIL, null);
        if (updateSupportEmail != null) {
            CustomMailMessageApi.updateLatestMailUID(updateSupportEmail, updateSupportEmail.getId());
        }
        return false;
    }
}
