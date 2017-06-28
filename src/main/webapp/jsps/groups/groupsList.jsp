<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">
       	Groups
       	<button data-toggle="modal" onclick="newGroup();" class="btn btn-outline btn-primary pull-right">
       		<i class="fa fa-plus"></i>
       		New Group
       	</button>
       	</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row users-list">
	<div class="col-lg-12">
   		<table class="table table-hover">
   			<thead>
   				<tr>
   					<th class="col-md-3 sortable">Name</th>
   					<th class="col-md-3 sortable align-right"><span class="line"></span>Description</th>
   					<th class="col-md-2 sortable"><span class="line"></span>Created</th>
   					<th class="col-md-2 sortable"><span class="line"></span>Status</th>
   					<th class="col-md-2 sortable"></th>
   				</tr>
   			</thead>
   			<tbody>
   				<s:iterator var="group" value="%{groups}">
					<tr>
	   					<td>
	                        <img src="http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/48/User-group-icon.png" alt="contact" class="img-circle avatar hidden-phone">
	                        <a href="#groups/<s:property value="#group.groupId" />" class="name"><s:property value="#group.name" /></a>
	                        <span class="subtext"><s:property value="#group.membersCount" /> members</span>
	                    </td>
	                    <td>
	                    	<s:property value="#group.description" />
	                    </td>
	                    <td><s:property value="#group.getFormattedCreatedTime()" /></td>
	                    <td>
	                    	<s:if test="%{#group.isActive()}">
								<h5><span class="label label-success">Active</span></h5>
							</s:if>
							<s:else>
								<h5><span class="label label-danger">Inactive</span></h5>
							</s:else>
	                    </td>
	                    <td class="center">
	                    	<div class="btn-group">
	                    		<button type="button" group-id="<s:property value="#group.groupId" />" data-toggle="tooltip" data-placement="left" title="Edit group" onclick="editGroup(this);" class="btn btn-outline btn-primary btn-md">
	                    			<i class="fa fa-pencil"></i>
	                    		</button>
	                    		<button type="button" group-id="<s:property value="#group.groupId" />" data-toggle="tooltip" data-placement="top" title="Delete group" onclick="confirmDelete(this);" class="btn btn-outline btn-danger btn-md">
	                    			<i class="fa fa-trash"></i>
	                    		</button>
	                    	</div>
	                    </td>
	                </tr>
			    </s:iterator>
             </tbody>
      </table>
  </div>
</div>
<div class="modal fade" id="newGroupModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">New Group</h4>
	        </div>
	        <div class="modal-body">
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveGroup(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<div class="modal fade" id="editGroupModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Edit Group</h4>
	        </div>
	        <div class="modal-body">
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="updateGroup(this);" class="btn btn-primary">Save</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<div class="modal fade" id="deleteConfirm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            <h4 class="modal-title" id="myModalLabel">Delete Group</h4>
	        </div>
	        <div class="modal-body">
	        Are you sure want to delete this group?
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	            <button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Deleting" onclick="deleteGroup(this);" class="btn btn-danger">Delete</button>
	        </div>
	    </div>
	    <!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
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
	
	function newGroup() {
		
		$("#newGroupModel").modal("show");
		$('#newGroupModel .modal-body').html("Loading...");
		
		$.ajax({
		      type: "GET",
		      url: contextPath + "/home/groups/new",
		      success: function (response) {
		    	  $('#newGroupModel .modal-body').html(response);
		      }
		 });
	}
	
	function saveGroup(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/groups/save",
			data : $("#newGroupForm").serialize()
		})
		.done(function(data) {
			$('#newGroupModel').modal('hide');
			FacilioApp.notifyMessage('success', 'Group created successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
	
	function editGroup(btn) {
		var groupId = $(btn).attr('group-id');
		
		$("#editGroupModel").modal("show");
		$('#editGroupModel .modal-body').html("Loading...");
		
		$.ajax({
		      type: "GET",
		      url: contextPath + "/home/groups/edit?id="+groupId,
		      success: function (response) {
		    	  $('#editGroupModel .modal-body').html(response);
		      }
		 });
	}
	
	function updateGroup(btn) {
		$(btn).button('loading');
		
		$.ajax({
			method : "post",
			url : contextPath + "/home/groups/update",
			data : $("#editGroupForm").serialize()
		})
		.done(function(data) {
			$('#editGroupModel').modal('hide');
			FacilioApp.notifyMessage('success', 'Group updated successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
	
	function confirmDelete(btn) {
		var groupId = $(btn).attr('group-id');
		
		$("#deleteConfirm .btn-danger").attr('group-id', groupId);
		
		$("#deleteConfirm").modal("show");
	}
	
	function deleteGroup(btn) {
		var groupId = $(btn).attr('group-id');
		
		$(btn).button('loading');
		
		var grp_obj = {'groupId': groupId};
		$.ajax({
			method : "post",
			url : contextPath + "/home/groups/delete",
			data : grp_obj
		})
		.done(function(data) {
			$('#deleteConfirm').modal('hide');
			FacilioApp.notifyMessage('success', 'Group deleted successfully!');
			
			setTimeout(function() {
				FacilioApp.refreshView();
            }, 500);
		})
		.fail(function(error) {
			$(btn).button('reset');
			alert(JSON.stringify(error.responseJSON.fieldErrors));
		});
	}
</script>