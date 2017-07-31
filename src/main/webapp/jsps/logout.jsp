<%
	String staticURL = "http://facilio-static.s3-website-us-west-2.amazonaws.com";
	session.invalidate();
%>
<!-- AWS Cognito -->
<script src="<%=staticURL%>/vendor/amazon-cognito-identity-js/aws-cognito-sdk.min.js"></script>
<script src="<%=staticURL%>/vendor/amazon-cognito-identity-js/amazon-cognito-identity.min.js"></script>
<script src="${pageContext.request.contextPath}/js/cognitoutil.js"></script>
<script>
	var cogUtil = new CognitoUtil();
	cogUtil.logout();
	
	location.href = "login";
</script>
Redirecting...