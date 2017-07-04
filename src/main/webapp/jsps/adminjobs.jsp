<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    
<%
 String staticURL = "http://facilio-static.s3-website-us-west-2.amazonaws.com";
 %>
<html>
	<head>
  		<title>Facilio Admin</title>  
  		<script src="<%=staticURL%>/vendor/jquery/jquery.min.js"></script>
  	</head>
<body>
	<div>
		<div><a href="/bms/home/admin">Admin Home</a></div>
		<div><br /></div>
		<div style="width:500px;" id="joblist">
			<div style="float:left; font-size: 18px;">Jobs :</div>
			<div onclick="$('#joblist').hide(); $('#addjob').show();" style="float:right; padding:5px; cursor:pointer; background:#f2f2f2; border:1px solid #ddd;">Add Job</div>
			<div style="clear:both;"></div>
			<div style="padding-top:10px;">
			<table border="0" cellspacing="0" cellpadding="0" style="border:1px solid #ddd; width:100%;">
				<tr>
		            <th style="text-align:left;padding:10px; border-bottom:1px solid #ddd; background: #f2f2f2;">Job Name</th>
		            <th style="text-align:left;padding:10px; border-bottom:1px solid #ddd; background: #f2f2f2;">Period</th>
		            <th style="text-align:left;padding:10px; border-bottom:1px solid #ddd; background: #f2f2f2;">Last Execution Time</th>
		            <th style="text-align:left;padding:10px; border-bottom:1px solid #ddd; background: #f2f2f2;"></th>
		            <th style="text-align:left;padding:10px; border-bottom:1px solid #ddd; background: #f2f2f2;"></th>
		        </tr>
				<s:iterator var="job" value="%{JOBS}">
		    		<tr>
			            <td style="padding:10px;"><s:property value="#job.name" /></td>
			            <td style="padding:10px;"><s:property value="#job.period" /></td>
			            <td style="padding:10px;"><s:property value="#job.lastexecutiontime" /></td>
			            <td style="padding:10px;"><a href="javascriot:void(0);" onclick="refreshJob('<s:property value="#job.jobId" />', '<s:property value="#job.name" />', <s:property value="#job.period" />);">Refresh</a></td>
			            <td style="padding:10px;"><a href="javascriot:void(0);" onclick="deleteJob('<s:property value="#job.jobId" />');">Delete</a></td>
			        </tr>
				</s:iterator>
				<s:if test="%{JOBS.isEmpty()}">
					<tr>
			            <td style="padding:10px; text-align:center;" colspan="4">No Jobs Found</td>
			        </tr>
				</s:if>
			</table>  
			</div>
		</div>          
	</div>
	<div id="addjob" style="display:none">
		<div>
			<div style="float:left; width:100px; padding-top:5px;">Job Name</div>
			<div style="float:left;"><input value="IotConnector" style="height:30px;" width="200px" name="jobname"/></div>
			<div style="clear:both;"></div>
		</div>
		<div style="padding-top:20px;">
			<div style="float:left; width:100px; padding-top:5px;">Period</div>
			<div style="float:left;"><input style="height:30px;" width="200px" name="period"/></div>
			<div style="clear:both;"></div>
		</div>
		<div style="padding-top:20px;">
			<div style="float:left; width:100px; padding-top:5px;"></div>
			<div style="float:left;"><input onclick="addJob();" style="height:30px;" type="submit" value="Submit"/></div>
			<div style="clear:both;"></div>
		</div>
	</div>
</body>
<script>
	function addJob(name, period)
	{
		var dataObject = new Object();
		if(name == undefined)
		{
			dataObject.name = $('input[name=jobname]').val();
		}
		else
		{
			dataObject.name = name;
		}
		if(period == undefined)
		{
			dataObject.period = $('input[name=period]').val();
		}
		else
		{
			dataObject.period = period;
		}
		
		$.ajax({
		      type: "POST",
		      url: "/bms/home/admin/addJob",   
		      data: dataObject,
		      success: function (response) {
		    	 window.location.reload();
		      }
		 });
	}
	
	function refreshJob(jobId, name, period)
	{
		deleteJob(jobId);
		addJob(name, period);
	}
	
	function deleteJob(jobId)
	{
		var dataObject = new Object();
		dataObject.jobId = jobId;
		$.ajax({
		      type: "POST",
		      url: "/bms/home/admin/deleteJob",   
		      data: dataObject,
		      success: function (response) {
		    	 window.location.reload();
		      }
		 });
	}
</script>
</html>