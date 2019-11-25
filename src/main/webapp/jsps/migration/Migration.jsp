<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.modules.FieldType,com.facilio.db.criteria.CriteriaAPI, com.facilio.db.criteria.operators.NumberOperators ,com.facilio.db.builder.GenericUpdateRecordBuilder,java.util.*,com.facilio.modules.FieldFactory,com.facilio.accounts.dto.*,com.facilio.modules.fields.FacilioField" %>

<%

GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();

FacilioField OrgIdField = new FacilioField();
OrgIdField.setName("orgId");
OrgIdField.setDisplayName("Org Id");
OrgIdField.setDataType(FieldType.NUMBER);
OrgIdField.setColumnName("ORGID");


List fields = new ArrayList<>();
fields.add(OrgIdField);
select.select(fields);
select.table("Organizations");

List<Map<String, Object>> orgids = select.get();

for(Map org :orgids) 
{
Long orgid = (Long) org.get("orgId");
AccountUtil.setCurrentAccount(orgid);

if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION))
{
	long featureLicenseModule = AccountUtil.getFeatureLicense();
	featureLicenseModule = featureLicenseModule + 67108864;	
	
	long licence = AccountUtil.getTransactionalOrgBean(orgid).addLicence(featureLicenseModule);

}
}

out.println("UPDATED");
%>
