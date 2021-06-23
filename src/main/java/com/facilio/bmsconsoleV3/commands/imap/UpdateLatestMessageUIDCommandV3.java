package com.facilio.bmsconsoleV3.commands.imap;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;
import org.apache.commons.chain.Context;

public class UpdateLatestMessageUIDCommandV3 extends FacilioCommand implements PostTransactionCommand {
    private Context context;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        SupportEmailContext updateSupportEmail = (SupportEmailContext) context.getOrDefault(FacilioConstants.ContextNames.SUPPORT_EMAIL, null);
        if (updateSupportEmail != null) {
            FacilioService.runAsService(FacilioConstants.Services.DEFAULT_SERVICE,() -> MailMessageUtil.updateLatestMailUID(updateSupportEmail, updateSupportEmail.getId()));
        }
        return false;
    }
}
