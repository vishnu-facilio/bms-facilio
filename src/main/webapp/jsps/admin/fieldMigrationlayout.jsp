<%--
  Created by IntelliJ IDEA.
  User: benitta
  Date: 02/04/20
  Time: 5:38 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="com.facilio.logging.SysOutLogger" %>
<%@page import="com.facilio.accounts.bean.OrgBean" %>
<%@page import=" com.facilio.bmsconsole.context.AssetCategoryContext" %>
<%@page import=" com.facilio.beans.ModuleBean" %>
<%@page import=" com.facilio.beans.ModuleCRUDBean" %>
<%@page import=" com.facilio.bmsconsole.context.AssetContext" %>
<%@page import="com.facilio.bmsconsole.util.AssetsAPI" %>
<%@page import=" com.facilio.modules.fields.FacilioField" %>
<%@page import="com.facilio.modules.FieldFactory" %>
<%@page import="com.facilio.modules.FacilioModule,com.facilio.accounts.dto.*" %>
<%@page import="com.facilio.modules.ModuleFactory" %>
<%@page import="com.facilio.chain.FacilioContext" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.ReadingRuleContext" %>
<%@page import="com.facilio.constants.FacilioConstants" %>
<%@page import="com.facilio.bmsconsole.actions.ReadingAction" %>
<%@page import="com.facilio.bmsconsole.actions.PickListAction" %>
<%@page import="com.facilio.modules.FieldType" %>
<%@page import=" com.facilio.modules.fields.NumberField" %>
<%@page import=" com.facilio.fw.BeanFactory" %>
<%@page
        import="com.facilio.accounts.util.AccountUtil,com.facilio.accounts.dto.Account,java.util.ArrayList,java.util.Comparator,com.facilio.accounts.dto.User,com.facilio.accounts.dto.Role, java.util.*, java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List, com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl, com.facilio.bmsconsole.commands.util.CommonCommandUtil, com.facilio.accounts.util.AccountUtil.FeatureLicense" %>
<%
    String orgid = request.getParameter("orgid");
    Organization org = null;
    OrgBean orgBean = AccountUtil.getOrgBean();
    ModuleCRUDBean bean = null;
    List<AssetCategoryContext> assetcategory = null;

    Map<Long, List<FacilioModule>> moduleMap = new HashMap<>();
    List<FacilioModule> reading = new ArrayList<>();

    List<AssetContext> AssetListOfCategory = new ArrayList<>();
    List<Organization> orgs = orgBean.getOrgs();

    if (orgid != null) {
        long orgId = Long.parseLong(orgid);
        bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        org = orgBean.getOrg(Long.parseLong(orgid));
        assetcategory = bean.getCategoryList();
    }


%>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript">
        function changeOrgPage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeThePage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeAssetPage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeselectPage() {
            var selectedOption = "?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeReadingPage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val();
            location.href = selectedOption;

        }

    </script>

    <script type="text/javascript">


        function changeEmailPage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "email=" + $("#email").val()
            s;

        }

        function changestartTTimePage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val() + "&" + "email=" + $("#email").val() + "&" + "fromTtime=" + $("#fromTtime").val();


        }

        function changeendTTimePage() {
            var selectedOption = "fieldMigration?orgid=" + $("#orgid").val() + "&" + "email=" + $("#email").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val() + "&" + "fromTtime=" + $("#fromTtime").val() + "&" + "toTtime=" + $("#toTtime").val();

        }
    </script>


    <meta charset="UTF-8">


    <title>Insert title here</title>
</head>
<body>
<form action="" method="GET">
    <h4>
        Shift Reading Tools
    </h4>
    <div class=" col-lg-8 col-md-8">

        <label for="orgid">
            <div class="admin-data-grey">Org:</div>
        </label>
        <select class="admin-data-select"
                name="orgid" id="orgid" onChange="changeOrgPage()">
            <option value="" disabled selected>Select</option>
            <%
                for (Organization domain : orgs) {
                    if (domain.getOrgId() == 321 || domain.getOrgId() == 1 || domain.getOrgId() == 155 || domain.getOrgId() == 210 || domain.getOrgId() == 146) {
            %>
            <option value="<%= domain.getId()%>"<%=(request.getParameter("orgid") != null && request.getParameter("orgid").equals(domain.getId() + "")) ? "selected" : " "%>><%=domain.getId()%>
                -
                <%=domain.getDomain()%>
            </option>
            <%
                    }
                }
            %>
        </select><br><br><br>


    </div>


</form>
<br>
<br>
<br>
<br>
<%
    if (org != null) {
%>
<form action="fieldMigrationAction">

    <div class=" col-lg-8 col-md-8">

        <input type="hidden" name="orgid" value="<%=org.getOrgId()%>">
        <br>
        <br>
        <br>
        <label for="assetcategory">
            <h5>AssetCategory:</h5>
        </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

        <select name="assetcategory" id="assetcategory" onChange="changeThePage()">
            <option value="" disabled selected>Select</option>
            <%
                for (AssetCategoryContext role : assetcategory) {
            %>
            <option value="<%=role.getId()%>"<%=(request.getParameter("assetcategory") != null && request.getParameter("assetcategory").equals(role.getId() + "")) ? "selected" : " "%>><%=role.getName()%>
            </option>
            <%
                }
            %>
        </select>
        <br>
        <br>
        <br>
        <label for="assetId"><h5>
            Asset:</h5></label>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp; <select
            name="assetId" id="assetId" onChange="changeAssetPage()">
        <option value="" disabled selected>Select</option>
        <%
            if ((request.getParameter("assetcategory") != null)) {
                long assetCategoryId = Long.parseLong(request.getParameter("assetcategory"));
                AssetListOfCategory = bean.getAssetListOfCategory(assetCategoryId);

                for (AssetContext list : AssetListOfCategory) {
        %>
        <option value="<%=list.getId()%>"<%=(request.getParameter("assetId") != null && request.getParameter("assetId").equals(list.getId() + "")) ? "selected" : " "%>><%=list.getName()%>
        </option>
        <%
                }
            }
        %>
    </select><br> <br> <br>
        <%--        <label for="selectfields"><h5>--%>
        <%--            Field Option:</h5></label>&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;&nbsp;<select--%>
        <%--            name="selectfields" id="selectfields" onChange="changeselectPage()">+--%>
        <%--        <option value="" disabled selected>Select</option>--%>
        <%--        <%if ((request.getParameter("assetcategory") != null)) {%>--%>
        <%--        <option value="<%=1%>"<%=(request.getParameter("selectfields") != null && request.getParameter("selectfields").equals("1" + "")) ? "selected" : " "%>>--%>
        <%--            Deltacalculate--%>
        <%--        </option>--%>
        <%--        <option value="<%=2%>"<%=(request.getParameter("selectfields") != null && request.getParameter("selectfields").equals("2" + "")) ? "selected" : " "%>>--%>
        <%--            RemoveDuplicate--%>
        <%--        </option>--%>
        <%--        <%} %>--%>
        <%--    </select>--%>

        <%--        <br> <br> <br>--%>

        <label for="sourceField"><h5>
           Source Reading :</h5></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&emsp;
        <select name="sourceField" id="sourceField">
            <option value="" disabled selected>Select</option>
            <%

                if ((request.getParameter("assetcategory") != null)) {
                    long parentCategoryId = Long.parseLong(request.getParameter("assetcategory"));
                    reading = bean.getAssetReadings(parentCategoryId);
                    for (FacilioModule list : reading) {
                        for (FacilioField fields : list.getFields()) {
            %>
            <option value="<%=fields.getId()%>"<%=(request.getParameter("fieldId") != null && request.getParameter("fieldId").equals(fields.getId() + "")) ? "selected" : " "%>><%=fields.getDisplayName()%>
            </option>

            <%
            }
            }
            }
            %>
        </select>
        <br>
        <br>
        <br>
        <label for="targetField"><h5>
            Target Reading :</h5></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&emsp;
        <select name="targetField" id="targetField">
            <option value="" disabled selected>Select</option>
            <%

                if ((request.getParameter("assetcategory") != null)) {
                    long parentCategoryId = Long.parseLong(request.getParameter("assetcategory"));
                    reading = bean.getAssetReadings(parentCategoryId);
                    for (FacilioModule list : reading) {
                        for (FacilioField fields : list.getFields()) {
            %>
            <option value="<%=fields.getId()%>"<%=(request.getParameter("fieldId") != null && request.getParameter("fieldId").equals(fields.getId() + "")) ? "selected" : " "%>><%=fields.getDisplayName()%>
            </option>

            <%
              //
            }
            }
            }
            %>
        </select>

        <br>
        <br>
        <br>
            <input type="submit" style="margin-left: 200px" name="fieldMigration" value="Submit"/>
        <br>
        <br>
        <br>

</form>
<br>
<br>
<br>
<br>
<%
    }
%>
</body>
<style>
    select {
        width: 30%;
        padding: 12px 20px;
        margin: 8px 16px;
        margin-top: 8px;
        display: inline-block;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
        font-size: 16px;
    }

    input[type=text] {
        width: 30%;
        padding: 12px 20px;
        margin: 8px 16px;
        margin-top: 8px;
        display: inline-block;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }

    input[type=datetime-local] {
        width: 30%;
        padding: 12px 20px;
        margin: 8px 16px;
        margin-top: 8px;
        display: inline-block;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }

    input[type=submit] {
        width: 27%;
        background-color: #4CAF50;
        color: white;
        padding: 12px 20px;
        margin: 2px 0;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 17px;
    }

    button[type=submit] {
        width: 20%;
        background-color: #4CAF50;
        color: white;
        padding: 12px 20px;
        margin: 2px 0;
        margin-left: 60px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 16px;
    }

    .admin-data-grey {
        color: #333;
        font-size: 13px;
        letter-spacing: 0.5px;
        font-weight: 400;
    }

    input[type=submit]:hover {
        background-color: #45a049;
    }
</style>
</html>