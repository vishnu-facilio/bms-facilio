<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="java.util.Collections"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.modules.FieldUtil"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder"%>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.bmsconsole.tenant.TenantSpaceContext" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.UpdateRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.util.TenantsAPI" %>
<%@ page import="com.facilio.bmsconsole.tenant.TenantContext" %>
<%@ page import="com.facilio.modules.SelectRecordsBuilder" %>

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
            try{
	            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	            FacilioModule module = modBean.getModule("tenantspaces");
	            	List<FacilioField> fields = modBean.getAllFields(module.getName());
				TenantSpaceContext tenantSpace = new TenantSpaceContext();
				tenantSpace.setModuleId(module.getModuleId());
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(module.getTableName())
						.fields(Collections.singletonList(FieldFactory.getModuleIdField(module)))
						.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
						;
				updateBuilder.update(FieldUtil.getAsProperties(tenantSpace));
			
            }
            catch(Exception e) {
            		e.printStackTrace();
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