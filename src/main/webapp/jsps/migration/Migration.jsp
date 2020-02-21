<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.FacilioStatus" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.bmsconsole.util.TicketAPI" %>
<%@ page import="com.facilio.bmsconsole.util.FormsAPI" %>
<%@ page import="com.facilio.bmsconsole.forms.FacilioForm" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.bmsconsole.forms.FormField" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Arrays" %>

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

            List<FacilioModule> modules = Arrays.asList(
                    modBean.getModule(FacilioConstants.ContextNames.TICKET_NOTES),
                    modBean.getModule(FacilioConstants.ContextNames.ASSET_NOTES),
                    modBean.getModule("vendorsNotes")
                    );
            for (FacilioModule module : modules) {
                if (module == null) {
                    continue;
                }
                FacilioField body = modBean.getField("body", module.getName());
                if (body == null) {
                    continue;
                }
                FacilioForm form = new FacilioForm();
                form.setName("default_" + module.getName() + "_web");
                form.setDisplayName("Add Notes");
                form.setFormType(FacilioForm.FormType.WEB);
                form.setLabelPosition(FacilioForm.LabelPosition.TOP);

                FormField formField = new FormField();
                formField.setField(body);
                formField.setDisplayName("Comment");
                formField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
                formField.setFieldId(body.getFieldId());

                List<FormField> formFields = Collections.singletonList(formField);
                form.setFields(formFields);

                FormsAPI.createForm(form, module);
            }
            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());

        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>