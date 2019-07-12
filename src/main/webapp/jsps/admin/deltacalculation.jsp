<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.accounts.bean.OrgBean"%>
<%@page import=" com.facilio.bmsconsole.context.AssetCategoryContext"%>
<%@page import=" com.facilio.beans.ModuleBean"%>
<%@page import=" com.facilio.beans.ModuleCRUDBean"%>
<%@page import=" com.facilio.bmsconsole.context.AssetContext"%>
<%@page import="com.facilio.bmsconsole.util.AssetsAPI"%>
<%@page import=" com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.chain.FacilioContext"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.ReadingRuleContext"%>
<%@page import="com.facilio.constants.FacilioConstants"%>
<%@page import="com.facilio.bmsconsole.actions.ReadingAction"%>
<%@page import="com.facilio.bmsconsole.actions.PickListAction"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import=" com.facilio.modules.fields.NumberField"%>
<%@page import=" com.facilio.fw.BeanFactory"%>
<%@page
	import="com.facilio.accounts.util.AccountUtil,com.facilio.accounts.dto.Account,java.util.ArrayList,java.util.Comparator,com.facilio.accounts.dto.User,com.facilio.accounts.dto.Role, java.util.*, java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List, com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl, com.facilio.bmsconsole.commands.util.CommonCommandUtil, com.facilio.accounts.util.AccountUtil.FeatureLicense"%>
<%
	String orgid = request.getParameter("orgid");
	Organization org = null;

	ModuleCRUDBean bean = null;
	List<AssetCategoryContext> assetcategory = null;

	Map<Long, List<FacilioModule>> moduleMap = new HashMap<>();
	List<FacilioModule> reading = new ArrayList<>();

	List<AssetContext> AssetListOfCategory = new ArrayList<>();
	if (orgid != null) {
		long orgId = Long.parseLong(orgid);
		bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		OrgBean orgBean = AccountUtil.getOrgBean();
		org = orgBean.getOrg(Long.parseLong(orgid));

		assetcategory = bean.getCategoryList();
	}
%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
	function changeOrgPage() {
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val();
		location.href = selectedOption;

	}
	function changeOrgPage() {
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val();
		location.href = selectedOption;

	}
</script>
<script type="text/javascript">
	function changeThePage() {
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
				+ "&" + "assetcategory=" + $("#assetcategory").val();
		location.href = selectedOption;

	}
</script>
<script type="text/javascript">
	function changeselectPage() {
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
				+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+  "assetId=" + $("#assetId").val()+"&"+"selectfields=" + $("#selectfields").val();
		location.href = selectedOption;

	}
</script>
<script type="text/javascript">
	function changeReadingPage() {
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
				+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+  "assetId=" + $("#assetId").val()+"&"+"selectfields=" + $("#selectfields").val()+"&"+"fieldId="+$("#fieldId").val();
		location.href = selectedOption;
	
	}
	function changeAssetPage(){
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
				+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+ "assetId=" + $("#assetId").val();
		location.href = selectedOption;

	}
	function changestartTTimePage(){
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
		+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+  "assetId=" + $("#assetId").val()+"&"+"selectfields=" + $("#selectfields").val()+"&"+"fieldId="+$("#fieldId").val()+"&"+"fromTtime="+$("#fromTtime").val();
		location.href = selectedOption;

	}
	function changeendTTimePage(){
		var selectedOption = "deltacalculation?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
		+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+  "assetId=" + $("#assetId").val()+"&"+"selectfields=" + $("#selectfields").val()+"&"+"fieldId="+$("#fieldId").val()+"&"+"fromTtime="+$("#fromTtime").val()+"&"+"toTtime="+$("#toTtime").val();
		location.href = selectedOption;

	}
	function changeRemoveDuplicateePage(){
		var selectedOption = "removeduplicates?orgid=" + $("#orgid").val()+"&"+"email="+$("#email").val()
		+ "&" + "assetcategory=" + $("#assetcategory").val()+"&"+  "assetId=" + $("#assetId").val()+"&"+"selectfields=" + $("#selectfields").val()+"&"+"fieldId="+$("#fieldId").val()+"&"+"fromTtime="+$("#fromTtime").val()+"&"+"toTtime="+$("#toTtime").val();
		location.href = selectedOption;

	}
</script>



<meta charset="UTF-8">


<title>Insert title here</title>
</head>
<body>
	<form action="" method="GET">
		<h2>
			<i class=" fa fa-building-o  fa-fw"></i>Org Info
		</h2>
		<div class=" col-lg-8 col-md-8">

			<div style="margin-top: 40px;"
				class="input-group col-lg-8 col-md-8 col-sm-8	">
				<span class="input-group-addon"><i
					class="glyphicon glyphicon-user"></i></span> <input id="orgid" type="text"
					value="<%=org == null ? "" : org.getId()%>" class="form-control" placeholder="OrgId"
					name="orgid" onChange="changeOrgPage" />
			</div>
			<br><br><br>
			<div style="margin-top: 40px;"
				class="input-group col-lg-8 col-md-8 col-sm-8	">
				<span class="input-group-addon"><i
					class="glyphicon glyphicon-user"></i></span> <input id="email" type="text"
					value="<%=request.getParameter("email")== null?" Email":request.getParameter("email") %>" class="form-control" 
					name="email" onChange="changeEmailPage" />
			</div>
			<div style="margin-top: 30px;">

				<button id="show" type="submit">Submit</button>
			</div>
		</div>
	</form>
	<br>
	<br>
	<br>
	<br>
	<%
		if (org != null) {
	%>
	<form action="deltaCalculation ">

		<div class=" col-lg-8 col-md-8">

			<input type="hidden" name="orgid" value="<%=org.getOrgId()%>">
			<input type="hidden" name="email" value="<%=request.getParameter("email")%>">
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
				<option value="<%=role.getId()%>"<%=(request.getParameter("assetcategory") != null && request.getParameter("assetcategory").equals(role.getId() + "")) ? "selected" : " "%>><%=role.getName()%></option>
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
				<option value="<%=list.getId()%>"<%=(request.getParameter("assetId") != null && request.getParameter("assetId").equals(list.getId() + "")) ? "selected" : " "%>><%=list.getName()%></option>
				<%
					}
						}
				%>
			</select><br> <br> <br>
			<label for="selectfields"><h5>
					Field Option:</h5></label>&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;&nbsp;<select
				name="selectfields" id="selectfields" onChange="changeselectPage()">+
				<option value="" disabled selected>Select</option>
				<%if((request.getParameter("assetcategory") != null)) {%>
				<option value="<%=1%>"<%=(request.getParameter("selectfields") != null && request.getParameter("selectfields").equals("1" + "")) ? "selected" : " "%>>Deltacalculate</option>
				<option value="<%=2%>"<%=(request.getParameter("selectfields") != null && request.getParameter("selectfields").equals("2" + "")) ? "selected" : " "%>>RemoveDuplicate</option>
				<%} %>
			</select><br> <br> <br>
			
			 <label for="fieldId"><h5>
					Reading :</h5></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&emsp;&emsp;&emsp;&emsp;&emsp;
			<select name="fieldId" id="fieldId">
				<option value="" disabled selected>Select</option>
				<% 
					
					if ((request.getParameter("assetcategory") != null)) {
							long parentCategoryId = Long.parseLong(request.getParameter("assetcategory"));
							reading = bean.getAssetReadings(parentCategoryId);
							for (FacilioModule list : reading) {
								for (FacilioField fields : list.getFields()) {
									if((request.getParameter("selectfields") != null) && (request.getParameter("selectfields").equalsIgnoreCase("1"))){
									if ((fields.getName().equals("totalEnergyConsumption")
											|| fields.getName().equals("phaseEnergyR")
											|| fields.getName().equals("phaseEnergyY")
											|| fields.getName().equals("phaseEnergyB"))
											|| (fields.getDataTypeEnum() == FieldType.NUMBER
													|| fields.getDataTypeEnum() == FieldType.DECIMAL)
													&& ((NumberField) fields).isCounterField()) {
				%>
				<option value="<%=fields.getId()%>"<%=(request.getParameter("fieldId") != null && request.getParameter("fieldId").equals(fields.getId() + "")) ? "selected" : " "%>><%=fields.getDisplayName()%></option>

				<%
					}
									}
									else{%>
											<option value="<%=fields.getId()%>"<%=(request.getParameter("fieldId") != null && request.getParameter("fieldId").equals(fields.getId() + "")) ? "selected" : " "%>><%=fields.getDisplayName()%></option>
								<% 	}
								}
							}
						}
				%>
			</select> 
			<br>
			<br>
			<br>
			<label for="fromTtime">
				<h5>EnterFrom TTIME:</h5>
			</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 
				<input type="text" id="fromTtime" value="<%=request.getParameter("fromTtime")==null?"": request.getParameter("fromTtime") %>" name="fromTtime" onChange="changestartTTimePage()"> 
			<br>
			<br>
			<br>
			
			<label for="toTtime">
			<h5>Enter to TTIME:</h5>
			</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
				<input type="text" id="toTtime" value="<%=request.getParameter("toTtime")==null?"": request.getParameter("toTtime")%>" name="toTtime" onChange="changeendTTimePage()">
			<br>
			<br>
			<br>
			<br>
				<input type="submit" style="margin-left: 200px"name="deltaCalculation" value="CalculateDelta" />
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

input[type=submit] {
	width: 27%;
	background-color: #4CAF50;
	color: white;
	padding: 12px 20px;
	margin: 2px 0;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	font-size: 15px;
}

input[type=submit]:hover {
	background-color: #45a049;
}
</style>
</html>