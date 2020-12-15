package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.V3MailMessageContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Message;
import java.util.*;

public class CustomMailMessageApi {

    private static final Logger LOGGER = LogManager.getLogger(CustomMailMessageApi.class.getName());

    public static void updateLatestMailUID(SupportEmailContext supportEmail, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSupportEmailsModule().getTableName())
                .fields(FieldFactory.getSupportEmailFields()).andCondition(
                        CriteriaAPI.getIdCondition(id, ModuleFactory.getSupportEmailsModule()));
        int rowupdate = builder.update(FieldUtil.getAsProperties(supportEmail));
        LOGGER.info("Updated" + rowupdate);
    }

    public static long createRecordToMailModule(SupportEmailContext supportMail, Message rawEmail) throws Exception {
        V3MailMessageContext mailMessage = V3MailMessageContext.instance(rawEmail);
        mailMessage.setParentId(supportMail.getId());
        Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
        if (mailMessage.getAttachmentsList().size() > 0) {
            attachments.put("mailAttachments", mailMessage.getAttachmentsList());
            mailMessage.setSubForm(attachments);
        }
        FacilioChain chain = TransactionChainFactory.getSaveMailMessage();

        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportMail);
        context.put(FacilioConstants.ContextNames.MESSAGES, Collections.singletonList(mailMessage));

        chain.execute();
        long recordId = (long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, -1);
        return recordId;
    }

}
