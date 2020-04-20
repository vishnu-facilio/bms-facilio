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
<%@ page import="com.facilio.modules.FieldType" %>

<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.BooleanField" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.modules.fields.*" %>
<%@ page import="com.facilio.tasker.job.JobStore" %>
<%@ page import="com.facilio.tasker.job.JobContext" %>
<%@ page import="com.facilio.accounts.dto.Account" %>


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
            FacilioModule assetDepreciationModule = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);
            if (assetDepreciationModule == null) {
                return false;
            }

            FacilioField startDateField = modBean.getField("startDate", assetDepreciationModule.getName());
            modBean.deleteField(startDateField.getFieldId());

            FacilioField startDate = FieldFactory.getField("startDateFieldId", "Start Date Field Id", "START_DATE_FIELDID", assetDepreciationModule, FieldType.NUMBER);
            startDate.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            startDate.setDefault(true);
            modBean.addField(startDate);

            FacilioField totalPrice = FieldFactory.getField("totalPriceFieldId", "Total Price Field Id", "TOTAL_PRICE_FIELDID", assetDepreciationModule, FieldType.NUMBER);
            totalPrice.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            totalPrice.setDefault(true);
            modBean.addField(totalPrice);

            FacilioField salvagePrice = FieldFactory.getField("salvagePriceFieldId", "Salvage Price Field Id", "SALVAGE_PRICE_FIELDID", assetDepreciationModule, FieldType.NUMBER);
            salvagePrice.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            salvagePrice.setDefault(true);
            modBean.addField(salvagePrice);

            FacilioField currentPrice = FieldFactory.getField("currentPriceFieldId", "Current Price Field Id", "CURRENT_PRICE_FIELDID", assetDepreciationModule, FieldType.NUMBER);
            currentPrice.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
            currentPrice.setDefault(true);
            modBean.addField(currentPrice);

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