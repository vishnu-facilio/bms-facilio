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

            LOGGER.info("Started FaultType For -- "+AccountUtil.getCurrentOrg().getId());
        	try{
                AggregatedEnergyConsumptionUtil.addFaultTypeField();
        	}
        	catch(Exception e){
                LOGGER.info("Error Occurred in adding FaultType For -- "+AccountUtil.getCurrentOrg().getId());	
        	}
            LOGGER.info("Completed FaultType For -- "+AccountUtil.getCurrentOrg().getId());
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