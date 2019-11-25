<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.accounts.util.AccountUtil, com.facilio.iam.accounts.util.IAMOrgUtil"%>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.modules.FieldType,com.facilio.db.criteria.CriteriaAPI, com.facilio.db.criteria.operators.NumberOperators ,com.facilio.db.builder.GenericUpdateRecordBuilder,java.util.*,com.facilio.modules.FieldFactory,com.facilio.accounts.dto.*,com.facilio.modules.fields.FacilioField" %>

<%

List<Organization> orgs = IAMOrgUtil.getOrgs();

for (Organization org:orgs) {
	
	if(org != null)
	{
		Account account = new Account(org, null);
		AccountUtil.setCurrentAccount(account);

		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION))
		{
			long featureLicenseModule = AccountUtil.getFeatureLicense();
			featureLicenseModule = featureLicenseModule + 67108864;	
			
			long licence = AccountUtil.getTransactionalOrgBean(org.getId()).addLicence(featureLicenseModule);

		}
		AccountUtil.cleanCurrentAccount();
	}
}

out.println("UPDATED");
%>
