<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.modules.FieldType,com.facilio.db.criteria.CriteriaAPI, com.facilio.db.criteria.operators.NumberOperators ,com.facilio.db.builder.GenericUpdateRecordBuilder,java.util.*,com.facilio.modules.FieldFactory,com.facilio.accounts.dto.*,com.facilio.modules.fields.FacilioField" %>

<%

List<Integer> orgids = Arrays.asList(1,2,39,53,58,60,63,75,78,81,88,90,92,93,95,99,104,107,108,109,111,112,113,114,116,120,122,125,129,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,163,164,165,168,169,170,171,172,173,174,176,177,178,179,180,181,182,183,184,185,186,187,188,190,191,192,193,194,195,196,197,198,199,200,201,203,204,205,206,207,208,209,210,211,212,213,214,218,219,220,223,224,225,226,227,228,231,232,233,236,237,238,239,240,241,242,243,244,246,247,248,249,250,251,252,253,254,267);
int norgids = orgids.size();

for (int i=0;i<norgids;++i) {
	
	Long orgId = orgids.get(i).longValue();
	if(orgId != null)
	{		
		AccountUtil.setCurrentAccount(orgId);

		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION))
		{
			long featureLicenseModule = AccountUtil.getFeatureLicense();
			featureLicenseModule = featureLicenseModule + 67108864;	
			
			long licence = AccountUtil.getTransactionalOrgBean(orgId).addLicence(featureLicenseModule);

		}		
	}
}

out.println("UPDATED");
%>
