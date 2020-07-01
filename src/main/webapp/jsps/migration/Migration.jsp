<%@page import="com.facilio.accounts.dto.Organization"%>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil" %>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.collections4.CollectionUtils" %>
<%@ page import="org.apache.commons.lang3.exception.ExceptionUtils" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FieldType" %>


<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule customMailMessages = new FacilioModule();
            customMailMessages.setName("customMailMessages");
            customMailMessages.setDisplayName("Custom Mail Message");
            customMailMessages.setTableName("CustomMailMessage");
            customMailMessages.setType(FacilioModule.ModuleType.BASE_ENTITY);
            long customMailMessagesId = modBean.addModule(customMailMessages);
            customMailMessages.setModuleId(customMailMessagesId);

            NumberField messageUID = new NumberField(customMailMessages,  "messageUID", "Message UID",  FacilioField.FieldDisplayType.NUMBER, "MESSAGE_UID", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(messageUID);
            NumberField supportId = new NumberField(customMailMessages,  "supportMailId", "Support Mail Id",  FacilioField.FieldDisplayType.NUMBER, "SUPPORT_MAIL_ID", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(supportId);
            FacilioField subject = new FacilioField(customMailMessages, "subject", "Subject" , FacilioField.FieldDisplayType.TEXTAREA, "MAIL_SUBJECT", FieldType.STRING, true, false, true, false );
            modBean.addField(subject);
            FacilioField from = new FacilioField(customMailMessages, "from", "From" , FacilioField.FieldDisplayType.TEXTAREA, "FROM_MAIL", FieldType.STRING, true, false, true, false );
            modBean.addField(from);
            FacilioField to = new FacilioField(customMailMessages, "to", "To" , FacilioField.FieldDisplayType.TEXTBOX, "TO_MAIL", FieldType.STRING, true, false, true, false );
            modBean.addField(to);
            FacilioField bcc = new FacilioField(customMailMessages, "bcc", "Bcc" , FacilioField.FieldDisplayType.TEXTBOX, "BCC", FieldType.STRING, true, false, true, false );
            modBean.addField(bcc);
            FacilioField contentType = new FacilioField(customMailMessages, "contentType", "Content Type" , FacilioField.FieldDisplayType.TEXTBOX, "CONTENT_TYPE", FieldType.STRING, true, false, true, false );
            modBean.addField(contentType);
            FacilioField content = new FacilioField(customMailMessages, "content", "Content" , FacilioField.FieldDisplayType.TEXTAREA, "CONTENT", FieldType.STRING, true, false, true, false );
            modBean.addField(content);
            NumberField sentDate = new NumberField(customMailMessages,  "sentDate", "Sent Date",  FacilioField.FieldDisplayType.NUMBER, "SENT_DATE", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(sentDate);
            NumberField receviedDate = new NumberField(customMailMessages,  "receviedDate", "Recevied Date",  FacilioField.FieldDisplayType.NUMBER, "RECEVIED_DATE", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(receviedDate);


            FacilioModule mailAttachments = new FacilioModule();
            mailAttachments.setName("mailAttachments");
            mailAttachments.setDisplayName("Message Mail Attachments");
            mailAttachments.setTableName("Mail_Attachments");
            mailAttachments.setType(FacilioModule.ModuleType.ATTACHMENTS);
            long mailAttachmentsId = modBean.addModule(mailAttachments);
            mailAttachments.setModuleId(mailAttachmentsId);
            modBean.addSubModule(customMailMessages.getModuleId(), mailAttachmentsId);

            NumberField fileId = new NumberField(mailAttachments,  "fileId", "File ID",  FacilioField.FieldDisplayType.NUMBER, "FILE_ID", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(fileId);
            NumberField messageId = new NumberField(mailAttachments,  "parentId", "Message Id",  FacilioField.FieldDisplayType.NUMBER, "MESSAGE_ID", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(messageId);
            NumberField createdTime = new NumberField(mailAttachments,  "createdTime", "Created Time",  FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.NUMBER ,true, false, true, false);
            modBean.addField(createdTime);

            LOGGER.info("Completed For -- "+AccountUtil.getCurrentOrg().getId());
            response.getWriter().println("Completed For -- "+AccountUtil.getCurrentOrg().getId());
            return false;
        }
    }
%>

<%
    try {
        List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
        if (CollectionUtils.isNotEmpty(orgs)) {
            for (Organization org : orgs) {
                AccountUtil.setCurrentAccount(org.getOrgId());
                FacilioChain c = FacilioChain.getTransactionChain();
                c.addCommand(new OrgLevelMigrationCommand());
                c.execute();

                AccountUtil.cleanCurrentAccount();
            }
        }
        response.getWriter().println("Migration done");
    }
    catch (Exception e) {
        response.getWriter().println("Error occurred");
        response.getWriter().println(ExceptionUtils.getStackTrace(e));
        LogManager.getLogger(OrgLevelMigrationCommand.class.getName()).error("Error occurred while running migration.", e);
    }
%>