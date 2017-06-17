<%
	String redirectURL = (String) request.getAttribute("redirect_url");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="refresh" content="0; url=<%=redirectURL %>" />
</head>
<body style="{margin: 0; padding: 0;}">
</body>
</html>