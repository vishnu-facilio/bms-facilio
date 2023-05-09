<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <meta name="robots" content="noindex, nofollow" />
        <title>Redirecting to SSO page, please wait...</title>
    </head>
    <body>
        <form method="post" id="acsForm" action="<%= request.getAttribute("IdentityACSURL") %>">
          <input type="hidden" name="SAMLResponse" value="<%= request.getAttribute("SAMLResponse") %>">
          <input type="hidden" name="RelayState" value="<%= request.getAttribute("RelayState") %>">
        </form>
    </body>
    <script type="text/javascript">
        if (document.querySelector("#acsForm") != null) {
            document.querySelector("#acsForm").submit();
        }
        else {
            setInterval(() => {
                if (document.querySelector("#acsForm") != null) {
                    document.querySelector("#acsForm").submit();
                }
                else {
                    console.log("#acsForm is null");
                }
            }, 1000);
        }
    </script>
</html>