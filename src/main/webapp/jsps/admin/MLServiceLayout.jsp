<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
    String usecase = request.getParameter("usecase");


%>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript">
        function changeOrgPage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function mlFormSubmitAlert(){
            alert("Form has been submitted. Please refer analytics page after sometime.");
        }
	</script>
    <script type="text/javascript">
        function changeUseCasePage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "usecase=" + $("#usecase").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeAssetPage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeModulePage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "moduleId=" + $("#moduleId").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeselectPage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val();
            location.href = selectedOption;

        }
    </script>
    <script type="text/javascript">
        function changeReadingPage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val();
            location.href = selectedOption;

        }

    </script>

    <script type="text/javascript">
    function scroll() {
      document.getElementById("block").style.color = " black";
    }
    

        function changeEmailPage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "email=" + $("#email").val()
            s;

        }

        function changestartTTimePage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val() + "&" + "email=" + $("#email").val() + "&" + "fromTtime=" + $("#fromTtime").val();


        }

        function changeendTTimePage() {
            var selectedOption = "mlService?orgid=" + $("#orgid").val() + "&" + "email=" + $("#email").val()
                + "&" + "assetcategory=" + $("#assetcategory").val() + "&" + "assetId=" + $("#assetId").val() + "&" + "selectfields=" + $("#selectfields").val() + "&" + "fieldId=" + $("#fieldId").val() + "&" + "fromTtime=" + $("#fromTtime").val() + "&" + "toTtime=" + $("#toTtime").val();

        }
     </script>
    <meta charset="UTF-8">
    <title>Insert title here</title>
</head>
<body>
<form action="" method="GET">
  
    <div class=" col-lg-8 col-md-8">

        <label for="orgid">
            <div class="admin-data-grey">Org</div>
        </label>
        &emsp;&emsp;&ensp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;
        
        <select class="admin-data-select"
                name="orgid" id="orgid">
            <option value="" disabled selected>Select</option>
            <%
                for (Organization domain : orgs) {
                 //   if (domain.getOrgId() == 321 || domain.getOrgId() == 1 || domain.getOrgId() == 75 || domain.getOrgId() == 155 || domain.getOrgId() == 210 || domain.getOrgId() == 349 || domain.getOrgId() == 78 || domain.getOrgId() == 343 || domain.getOrgId() == 473 ||  domain.getOrgId() == 393 || domain.getOrgId() == 352 || domain.getOrgId() == 418) {

            %>
            <option value="<%= domain.getId()%>"
            <%=(request.getParameter("orgid") != null && request.getParameter("orgid").equals(domain.getId() + "")) ? "selected" : " "%>><%=domain.getId()%>
                -
                <%=domain.getDomain()%>
            </option>
            <%
//                    }
                }
            %>
        </select><br><br><br>
                <label for="usecase">
            <h5>Usecase</h5>
        </label> &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;
          <select
                name="usecase" id="usecase" onChange="changeUseCasePage()">
	
        		<option value="" disabled selected>Select Usecase</option>
        		<option value="energyanomaly"
        		<%=(request.getParameter("usecase") != null && request.getParameter("usecase").equals("energyanomaly")) ? "selected" : " "%>
        		>Energy Anomaly</option>
        		<option value="energyprediction"<%=(request.getParameter("usecase") != null && request.getParameter("usecase").equals("energyprediction")) ? "selected" : " "%>>Energy Prediction</option>
        		<option value="loadprediction"<%=(request.getParameter("usecase") != null && request.getParameter("usecase").equals("loadprediction")) ? "selected" : " "%>>Load Prediction</option>
            	<option value="multivariateanomaly"<%=(request.getParameter("usecase") != null && request.getParameter("usecase").equals("multivariateanomaly")) ? "selected" : " "%>>Multivariate Anomaly</option>
            	
            </select>
           
    </div>
</form>
<br>
<br>
<br>
<br>
</form>
<%
     if ((org != null) && (!usecase.equalsIgnoreCase(""))) {
%>            
<form action="mlServiceAction">

    <div class=" col-lg-8 col-md-8">


   	 <input type="hidden" name="orgid" value="<%=org.getOrgId()%>">
        <br>
        <br>        
        <input type="hidden" name="usecase" value="<%=usecase%>">
               	 <br>
               	 <br>
      <div class="useinput">
        <label for="scenario"><h5>Prediction name</h5></label>
            &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
        
        <textarea id="scenario" name="scenario" placeholder="Enter Prediction name" rows=100 cols=1000/></textarea>
      </div><br><br><br>
      

<%
     if ((org != null) && (usecase.equalsIgnoreCase("multivariateanomaly"))) {
%>            		
 		<div class="useinput">
        <label for="model"><h5>Readings</h5></label>
         		&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&nbsp;
        
        <textarea id="model" name="model" placeholder="Enter Readings" rows=100 cols=1000/></textarea>
      </div><br><br><br>

<%
    } else {
%>
	
	<div class="useinput">
        <label for="assetIdList"><h5>Asset ID</h5></label>
         		&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&nbsp;
        
        <textarea id="assetIdList" name="assetIdList" placeholder="Enter Asset ID" rows=100 cols=1000/></textarea>
      </div><br><br><br>
<%
}%>
       	<div class="useinput">
        <label for="modelvariables"><h5>Model Details</h5></label>
        &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;&nbsp;
        <textarea id="modelvariables" name="modelvariables" placeholder="Enter here" rows=100 cols=1000/></textarea>
      </div><br><br><br>
      <%
     if ((org != null) && (usecase.equalsIgnoreCase("multivariateanomaly"))) {
%>
       	<div class="useinput">
        <label for="groupingmethod"><h5>Readings Grouping Details</h5></label>
        &emsp;&emsp;&emsp;&emsp;&emsp;
        <textarea id="groupingmethod" name="groupingmethod" placeholder="Enter here" rows=100 cols=1000/></textarea>
      </div><br><br><br>
       <div class="useinput">
        <label for="filtermethod"><h5>Readings Condition Details</h5></label>
        &emsp;&emsp;&emsp;&emsp;&emsp;
        <textarea id="filtermethod" name="filtermethod" placeholder="Enter here" rows=100 cols=1000/></textarea>
      </div><br><br><br>

<%
}%>
       <div class="useinput">
        <label for="workflowinfo"><h5>Workflow Info</h5></label>
        &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;
        <textarea id="workflowinfo" name="workflowinfo" placeholder="Enter here" rows=100 cols=1000/></textarea>
      </div><br><br><br>
      <label for="fromTtime">
            <h5>Start Time (Org Time):</h5>
        </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;&nbsp;&nbsp;

        <input type="datetime-local" id="fromTtime"
               value="<%=request.getParameter("fromTtime")==null?"": request.getParameter("fromTtime") %>"
               name="fromTtime">
        <br>
        <br>
        <br>

        <label for="toTtime">
            <h5>End Time (Org Time):</h5>
        </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;

        <input type="datetime-local" id="toTtime"
               value="<%=request.getParameter("toTtime")==null?"": request.getParameter("toTtime")%>" name="toTtime">
        <br>
        <br>
        <br>
        <br>
        <input type="submit" style="margin-left: 100px" onclick="mlFormSubmitAlert()" name="mlService" value="Submit"/>
        <br>
        <br>
        <br>
</div>

</form>
<br>
<br>
<br>
<br>
<%
}%>
</body>
<style>
.useinput label, .useinput textarea {
    float: none; /* if you had floats before? otherwise inline-block will behave differently */
    display: inline-block;
    vertical-align: middle;    
}
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
    textarea {
        width: 30%;
        height:50px;
        padding: 12px 20px;
        margin: 8px 16px;
		font-size : 13px;      
        display: inline-block;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
        overflow: hidden;
	  overflow-style: marquee;
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
