<%@page import="com.facilio.db.criteria.operators.CommonOperators"%>
<%@page import="com.facilio.db.criteria.operators.Operator"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder"%>
<%@page import="com.facilio.modules.FieldUtil"%>
<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioChainFactory"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantSpaceContext"%>
<%@page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.db.criteria.operators.BooleanOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="com.facilio.bmsconsole.util.TenantsAPI"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%
	final class MigrationCommand extends FacilioCommand {
		@Override
		public boolean executeCommand(Context context) throws Exception {
			final Logger LOGGER = LogManager.getLogger(MigrationCommand.class.getName());
			try{
			List<Organization> organizations = AccountUtil.getOrgBean().getOrgs();
			
			
			for (Organization org : organizations) {
				Long orgId = org.getOrgId();
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(orgId);
				
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				System.out.println("mig JSP running");
				
				FacilioModule baseSpaceModule=modBean.getModule("basespace");
				
				FacilioModule visitorLogModule=modBean.getModule("visitorlogging");
				FacilioModule visitorModule=modBean.getModule("visitor");
				
				FacilioField visitedSpace=modBean.getField("visitedSpace", "visitorlogging");				
				FacilioField lastVisitedSpace=modBean.getField("lastVisitedSpace", "visitor");
				
				if(visitorLogModule!=null&&visitorModule!=null&&visitedSpace!=null&&lastVisitedSpace!=null&&baseSpaceModule!=null)
				{
					
					
					Map<String,Object> propMap=new HashMap<>();
					propMap.put("lookupModuleId",baseSpaceModule.getModuleId());
					
					FacilioModule lookupFieldsModule=ModuleFactory.getLookupFieldsModule();
					GenericUpdateRecordBuilder builder=new GenericUpdateRecordBuilder().table(lookupFieldsModule.getTableName())
					.fields(FieldFactory.getLookupFieldFields()).andCondition(
				 CriteriaAPI.getCondition("FIELDID", "fieldId",lastVisitedSpace.getFieldId()+","+visitedSpace.getFieldId() ,
						 NumberOperators.EQUALS));					
					builder.update(propMap);
					
					
				}
				
				

				
				
				
				
				
			
			}
		}
			catch(Exception e) {
				LOGGER.error("Error occurred", e);
				throw e;
			}
			return false;	
		}
	}
%>

<%
	FacilioChain c = FacilioChain.getTransactionChain();
	c.addCommand(new MigrationCommand());
	c.execute();
%>


