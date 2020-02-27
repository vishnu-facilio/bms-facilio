<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>

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
            FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
            FacilioModule spaceRatingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_RATING);
            if (spaceRatingModule != null) {
                return false;
            }
            if (baseSpaceModule != null) {
                FacilioModule ratingModule = new FacilioModule();
                ratingModule.setName(FacilioConstants.ContextNames.SPACE_RATING);
                ratingModule.setDisplayName("Space Rating");
                ratingModule.setTableName("Rating");
                ratingModule.setType(FacilioModule.ModuleType.RATING);

                long moduleId = modBean.addModule(ratingModule);
                ratingModule.setModuleId(moduleId);

                FacilioField nameField = FieldFactory.getField("name", "Name", "NAME", ratingModule, FieldType.STRING);
                modBean.addField(nameField);

                FacilioField descriptionField = FieldFactory.getField("description", "Description", "DESCRIPTION", ratingModule, FieldType.STRING);
                modBean.addField(descriptionField);

                FacilioField ratingField = FieldFactory.getField("ratingValue", "Rating Value", "RATING_VALUE", ratingModule, FieldType.NUMBER);
                modBean.addField(ratingField);

                FacilioField parentField = FieldFactory.getField("parent", "Parent", "PARENT_ID", ratingModule, FieldType.LOOKUP);
                ((LookupField) parentField).setLookupModule(baseSpaceModule);
                modBean.addField(parentField);
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