<%@page import="com.facilio.bmsconsole.util.ReadingsAPI"%>
<%@page import="com.facilio.bmsconsole.context.ReadingDataMeta"%>
<%@page import="com.facilio.constants.FacilioConstants"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="com.facilio.bmsconsole.util.ApplicationApi"%>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@ page import="org.apache.commons.chain.Context" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder" %>
<%@ page import="com.facilio.accounts.util.AccountConstants" %>
<%@ page import="com.facilio.db.criteria.CriteriaAPI" %>
<%@ page import="com.facilio.db.criteria.operators.NumberOperators" %>
<%@ page import="org.apache.commons.lang3.tuple.Pair" %>
<%@ page import="com.google.cloud.Tuple" %>
<%@ page import="com.facilio.iam.accounts.util.IAMUtil" %>
<%@ page import="com.facilio.iam.accounts.util.IAMUserUtil" %>
<%@ page import="com.facilio.accounts.util.UserUtil" %>
<%@ page import="com.facilio.modules.FieldUtil" %>
<%@ page import="com.facilio.modules.FieldFactory" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="com.facilio.db.builder.GenericUpdateRecordBuilder" %>
<%@ page import="com.facilio.db.criteria.operators.StringOperators" %>
<%@ page import="org.apache.commons.lang3.tuple.MutablePair" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.aws.util.FacilioProperties" %>
<%@ page import="com.facilio.db.transaction.FacilioConnectionPool" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@page import="com.facilio.iam.accounts.util.IAMAccountConstants"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.facilio.service.FacilioService"%>
<%@ page import="com.facilio.accounts.dto.Account" %>
<%@ page import="com.facilio.modules.fields.*" %>


<%--

  ___                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | _    _ _   __ | |_    __| |_   _ _ _ _   _ _  __  | |_| |__   _  | |__   _ _ __  _  | |_ _  _ _ _ _ _   _ _| |
 | |  | |/ _ \  | '_ \ / _ \| _|  / __| ' \ / ` | ' \ / ` |/ _ \ | __| ' \ / _ \ | '_ \ / ` / __|/ _ \ |  _/ _ \| '__| ' ` _ \ / _` | __|
 | |__| | () | | | | | () | |_  | (_| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |) | (| \_ \  _/ | || () | |  | | | | | | (| | |
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>




<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

    		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    		
    		FacilioField field = modbean.getField(1103848l);
            
    		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
    				.select(FieldFactory.getControlPointFields())
    				.table(ModuleFactory.getControlPointModule().getTableName())
    				;
    		
    		List<Map<String,Object>> props = builder.get();
    		
    		for(Map<String,Object> itr:props){
				long id = (long)itr.get("id");
				
				ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(id);
				ReadingDataMeta childRDM = ReadingsAPI.getReadingDataMeta(rdm.getResourceId(), field);
				
				GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
						.fields(FieldFactory.getControlPointFields())
						.table(ModuleFactory.getControlPointModule().getTableName())
						.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getControlPointModule()));
				
				Map<String,Object> updateProps = new HashMap<String,Object>();
				updateProps.put("childRDMId", childRDM.getId());
				update.update(updateProps);
    		}
    		
            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
    	if(org.getOrgId() == 297l) {
    		AccountUtil.setCurrentAccount(org.getOrgId());

            FacilioChain c = FacilioChain.getTransactionChain();
            c.addCommand(new OrgLevelMigrationCommand());
            c.execute();
            AccountUtil.cleanCurrentAccount();
    	}
    }
%>