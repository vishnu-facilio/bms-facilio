


<%@page import="com.facilio.constants.FacilioConstants.ApplicationLinkNames"%>
<%@page import="com.facilio.bmsconsole.util.ApplicationApi"%>
<%@page import="com.facilio.bmsconsole.context.ApplicationContext"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="com.facilio.bmsconsole.context.SingleSharingContext.SharingType"%>
<%@page import="java.util.Collections"%>
<%@page import="com.facilio.bmsconsole.util.SharingAPI"%>
<%@page import="com.facilio.bmsconsole.context.SingleSharingContext"%>
<%@page import="com.facilio.bmsconsole.context.SharingContext"%>
<%@page import="com.facilio.bmsconsole.util.LookupSpecialTypeUtil"%>
<%@page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@page import="com.facilio.db.criteria.Criteria"%>
<%@page import="com.facilio.bmsconsole.view.ColumnFactory"%>
<%@page import="com.facilio.bmsconsole.view.SortField"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.bmsconsole.view.FacilioView.ViewType"%>
<%@page import="com.facilio.bmsconsole.view.ViewFactory"%>
<%@page import="com.facilio.bmsconsole.context.ViewField"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateByIdContext"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateContext"%>
<%@page import="com.facilio.constants.FacilioConstants"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder"%>
<%@page import="com.facilio.bmsconsole.view.FacilioView"%>
<%@page import="com.facilio.bmsconsole.util.ViewAPI"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.facilio.bmsconsole.context.ViewGroups"%>
<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.modules.FieldUtil"%>
<%@page import="com.facilio.accounts.dto.IAMUser"%>
<%@page import="java.util.Map"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.chain.FacilioContext"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.bmsconsole.util.AggregatedEnergyConsumptionUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.command.FacilioCommand" %>
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
<%@ page import="com.facilio.bmsconsole.commands.FacilioChainFactory" %>


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
            long orgId = AccountUtil.getCurrentOrg().getId();
            printMsg("Started For -- "+AccountUtil.getCurrentOrg().getId());
            // write code here
            printMsg("Completed For -- "+AccountUtil.getCurrentOrg().getId());

            return false;
        }

        // to print in both logs and web
        private void printMsg(String message) throws Exception {
            LOGGER.info(message);
            response.getWriter().println(message+"<br>");
        }

    }
%>

<%
    try {
        List<Organization> orgs = AccountUtil.getOrgBean().getOrgsForMigration();
        response.getWriter().println("orgs count ::"+orgs.size()+"<br>");
        if (CollectionUtils.isNotEmpty(orgs)) {
            for (Organization org : orgs) {
                if (org.getOrgId() > 0) {

                    AccountUtil.setCurrentAccount(org.getOrgId());
                    FacilioChain c = FacilioChain.getTransactionChain();
                    c.addCommand(new OrgLevelMigrationCommand());
                    c.execute();

                    AccountUtil.cleanCurrentAccount();

                }
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