package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.MailContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.V3MailMessageContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Message;
import java.util.*;

public class CustomMailMessageApi {

    private static final Logger LOGGER = LogManager.getLogger(CustomMailMessageApi.class.getName());


    public static void insertCustomMailMessage(List<MailContext> messages) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("customMailMessages");
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(module.getFields())
                .addRecords(FieldUtil.getAsMapList(messages, MailContext.class));
        builder.save();
    }

    public static void insertV3CustomMailMessage(List<V3MailMessageContext> messages) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("customMailMessages");
        List<FacilioField> fields = modBean.getAllFields("customMailMessages");
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .addRecords(FieldUtil.getAsMapList(messages, V3MailMessageContext.class));
        builder.save();

    }

    public static long fetchLatestMessageUID(long suppportMailId) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("customMailMessages");
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(module.getFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(suppportMailId, module))
                .orderBy("ID")
                .limit(1);
        List<Map<String, Object>> props = selectBuilder.get();
        MailContext lastestMail = null;
        if (props != null && !props.isEmpty()) {
            lastestMail = FieldUtil.getAsBeanFromMap(props.get(0), MailContext.class);
            return lastestMail.getMessageUID();
        } else {
            return -1;
        }

    }

    public static void updateLatestMailUID(SupportEmailContext supportEmail, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSupportEmailsModule().getTableName())
                .fields(FieldFactory.getSupportEmailFields()).andCondition(
                        CriteriaAPI.getIdCondition(id, ModuleFactory.getSupportEmailsModule()));
        int rowupdate = builder.update(FieldUtil.getAsProperties(supportEmail));
        LOGGER.info("Updated" + rowupdate);
    }

    public static void createRecordToMailModule(SupportEmailContext supportMail, Message rawEmail) throws Exception {
        V3MailMessageContext mailMessage = V3MailMessageContext.instance(rawEmail);
        mailMessage.setParentId(supportMail.getId());
        Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
        if (mailMessage.getAttachmentsList().size() > 0) {
            attachments.put("mailAttachments", mailMessage.getAttachmentsList());
            mailMessage.setSubForm(attachments);
        }
        FacilioChain chain = TransactionChainFactory.getSaveMailMessage();

        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MESSAGES, Collections.singletonList(mailMessage));

        chain.execute();
    }

}
