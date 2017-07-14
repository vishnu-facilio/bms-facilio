<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row users-list">
	<div class="col-lg-12">
   		<table class="table table-hover">
   			<thead>
   				<tr>
   					<th class="col-md-3 sortable">Name</th>
   					<th class="col-md-3 sortable align-right"><span class="line"></span>Email</th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="user" value="%{users}">
					<tr>
	   					<td>
	   						<input name="userId" type="hidden" value="<s:property value="#user.userId" />" />
	   						<input name="userName" type="hidden" value="<s:property value="#user.name" />" />
	                        <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="contact" class="img-circle avatar hidden-phone">
	                        <a href="javascript:void(0);" class="name"><s:property value="#user.name" /></a>
	                        <span class="subtext"><s:property value="#user.getRoleAsString()" /></span>
	                    </td>
	                    <td>
	                    	<a href="javascript:void(0);"><s:property value="#user.email" /></a>
	                    </td>
	                </tr>
			    </s:iterator>
             </tbody>
      </table>
  </div>
</div>
<style>
.users-list .table {
	border: 1px solid #ddd;
}

.users-list .table td {
	padding:15px;
}

.users-list .table img.avatar {
    float: left;
    margin-right: 14px;
    max-width: 45px;
    position: relative;
    top: 5px;
}

.img-circle {
    border-radius: 50%;
}

img {
    vertical-align: middle;
}

img {
    border: 0;
}

.users-list .table a.name {
    display: block;
    font-size: 14px;
    margin: 8px 0 0 0;
}
.users-list .table .subtext {
    font-size: 12px;
    margin-left: 0;
    color: #778391;
    font-style: italic;
    margin-top: 0;
}
.users-list .table td {
    vertical-align: middle;
    font-size: 13px;
}
</style>
<script>
	$(".users-list table").dataTable();

	$('.users-list').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
	
	$(".users-list tbody tr").click( function() {
		FacilioApp.selectValue($(this).find('input[name=userId]').val(), $(this).find('input[name=userName]').val(), $('#userspopup'));
	});
</script>