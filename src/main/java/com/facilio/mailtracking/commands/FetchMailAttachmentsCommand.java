package com.facilio.mailtracking.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.OutgoingMailAPI;
import com.facilio.mailtracking.context.V3OutgoingMailAttachmentContext;
import com.facilio.mailtracking.context.V3OutgoingMailLogContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FetchMailAttachmentsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3OutgoingMailLogContext> mailContexts = Constants.getRecordList((FacilioContext) context);
        if(!CollectionUtils.isEmpty(mailContexts)) {
            for (V3OutgoingMailLogContext mail : mailContexts) {
                List<V3OutgoingMailAttachmentContext> attachmentList = OutgoingMailAPI.getMailAttachments(mail.getId());
                mail.setAttachmentsList(FieldUtil.getAsMapList(attachmentList, V3OutgoingMailAttachmentContext.class));
            }
        }
        return false;
    }
}
