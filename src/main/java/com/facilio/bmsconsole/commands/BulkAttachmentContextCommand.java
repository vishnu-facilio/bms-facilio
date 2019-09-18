package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.constants.FacilioConstants;

public class BulkAttachmentContextCommand implements Command {
    @Override
    public boolean execute(Context context) throws Exception {
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);
        List<List<AttachmentContext>> attachments = bulkWorkOrderContext.getAttachments();

        if (CollectionUtils.isEmpty(attachments)) {
            return false;
        }

        List<AttachmentContext> workOrderAttachments = new ArrayList<>();

        for (int i = 0; i < attachments.size(); i++) {
            for (int j = 0; j < attachments.get(i).size(); j++) {
                workOrderAttachments.add(attachments.get(i).get(j));
            }
        }

        context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, workOrderAttachments);
        return false;
    }
}
