<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.modules.FieldType,com.facilio.db.criteria.CriteriaAPI, com.facilio.db.criteria.operators.NumberOperators ,com.facilio.db.builder.GenericUpdateRecordBuilder,java.util.*,com.facilio.modules.FieldFactory,com.facilio.accounts.dto.*,com.facilio.modules.fields.FacilioField" %>

<%

List<Long> orgids = new ArrayList<Long>();

for(long i=1; i<300; i++)
{
	orgids.add(i);
}

for(Long orgId :orgids) 
{
AccountUtil.setCurrentAccount(orgId);

if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION))
{
	long featureLicenseModule = AccountUtil.getFeatureLicense();
	featureLicenseModule = featureLicenseModule + 67108864;	
	
	long licence = AccountUtil.getTransactionalOrgBean(orgId).addLicence(featureLicenseModule);

}
}

out.println("UPDATED");
%>
