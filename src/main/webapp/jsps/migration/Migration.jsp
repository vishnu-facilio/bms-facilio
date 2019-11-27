<%@page import="com.facilio.modules.ModuleFactory, com.facilio.bmsconsole.commands.HistoricalVMForRMZ"%>
<%@page import="com.facilio.accounts.util.AccountUtil, com.facilio.iam.accounts.util.IAMOrgUtil"%>
<%@ page import="com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.modules.FieldType,com.facilio.db.criteria.CriteriaAPI, com.facilio.db.criteria.operators.NumberOperators ,com.facilio.db.builder.GenericUpdateRecordBuilder,java.util.*,com.facilio.modules.FieldFactory,com.facilio.accounts.dto.*,com.facilio.modules.fields.FacilioField" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

try
{
	String VMstoRun = request.getParameter("vmId");
	
	if (VMstoRun != null) {
		AccountUtil.setCurrentAccount(78);
		HistoricalVMForRMZ.runHistoricalForRMZ(Arrays.asList(VMstoRun.split(",")));
	}

	out.println("UPDATED");
}
catch(Exception e)
{
	out.println("Exception Occurred " + e);
}

%>
