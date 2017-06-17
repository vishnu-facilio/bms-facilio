<%@ taglib prefix="s" uri="/struts-tags" %>
<%
String samlResponse = (String) request.getAttribute("SAMLResponse");
String AssertionConsumerServiceURL = (String) request.getAttribute("AssertionConsumerServiceURL");
String relay = (String) request.getAttribute("relay");
%>
<html>
<body onload="document.getElementById('saml_resp').submit();">
Redirecting....
<form id="saml_resp" method="post" action="<%=AssertionConsumerServiceURL%>" style="display:none;">
	<input type="hidden" name="SAMLResponse" value="<%= samlResponse %>" />
    <input type="hidden" name="RelayState" value="<%= relay %>" />
    <input type="submit" value="Submit" />
  </form>
</body>
</html>