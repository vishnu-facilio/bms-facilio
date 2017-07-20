<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Roles
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row role-list">
	<div class="col-lg-12">
   		<table class="table table-hover">
   			<thead>
   				<tr>
   					<th class="col-md-1 sortable">Name</th>
   					<th class="col-md-3 sortable"><span class="line"></span>Description</th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="role" value="%{roles}">
					<tr>
	   					<td>
	                        <a href="#role/<s:property value="#role.roleId" />" class="name"><s:property value="#role.name" /></a>
	                    </td>
	                    <td><s:property value="#role.description" /></td>
	                </tr>
			    </s:iterator>
             </tbody>
      </table>
  </div>
</div>
<style>
.role-list .table {
	border: 1px solid #ddd;
}

.role-list .table td {
	padding:15px;
}

.role-list .table img.avatar {
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

.role-list .table a.name {
    display: block;
    font-size: 14px;
    margin: 8px 0 0 0;
}
.role-list .table .subtext {
    font-size: 12px;
    margin-left: 0;
    color: #778391;
    font-style: italic;
    margin-top: 0;
}
.role-list .table td {
    vertical-align: middle;
    font-size: 13px;
}
</style>
<script>
	$(".role-list table").dataTable();

	$('.role-list').tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });
</script>