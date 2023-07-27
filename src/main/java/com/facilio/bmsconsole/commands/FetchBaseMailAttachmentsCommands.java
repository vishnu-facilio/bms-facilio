package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FetchBaseMailAttachmentsCommands extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BaseMailMessageContext> mailContexts = Constants.getRecordList((FacilioContext) context);
        if(!CollectionUtils.isEmpty(mailContexts)) {
                for (BaseMailMessageContext mail : mailContexts) {
                    List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(mail.getId(), MailMessageUtil.MAIL_ATTACHMENT_MODULE);
                    if(attachments != null) {
                        mail.setAttachmentsList(FieldUtil.getAsMapList(attachments, AttachmentV3Context.class, false));
                    }
                }
        }
        return false;
    }
}
