<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<table width="100%" class="table table-striped able-hover" id="record-list">
    <thead>
        <tr>
        	<th>
        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        		Name
        	</th>
        	<th>Description</th>
        	<th>Created</th>
        	<th>Status</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    	<s:iterator var="group" value="%{groups}">
			<tr class="odd gradeX" data-group-id="<s:property value="#group.groupId" />">
  					<td>
                       <span class="group-name"><s:property value="#group.name" /></span>
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
                   		<div class="dropdown more-actions">
	                   		<a data-toggle="dropdown" class="dropdown-toggle"><i class="fa fa-ellipsis-h"></i></a>
	                   		<ul class="dropdown-menu" role="menu">
	                           	<li><a class="edit">Edit</a>
								<li><a class="delete">Delete</a>
	                        </ul>
                        </div>
                   </td>
               </tr>
	    </s:iterator>
	</tbody>
</table>
<script>
	jQuery(function($) {
		var $userName = $('.group-name');
		if ($userName.length) {
			$userName.avatarMe({
				avatarClass: 'avatar-me',
				max: 2
	  		});
		}
	});
	
	$(".setup-list-container table").dataTable({
		order: [[0, 'asc']],
	      language: {
	    	  paginate: {
	    		  previous: "<",
	    		  next: ">"
	    	  }
	      },
	      
	      columnDefs: [{
	    	  targets: 1,
	    	  orderable: false
	      },
	      {
	    	  targets: 3,
	    	  orderable: false
	      },
	      {
	    	  targets: 4,
	    	  searchable: false,
	    	  orderable: false
	      }],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      paging: false,
	      lengthChange: false,
	});
	
	$(".action-btn .new-btn").click(function() {
		location.href = '#groups/new';
	});
	
	$(".more-actions .edit").click(function() {
		// edit group
		var groupId = $(this).closest('tr').data('group-id');
		location.href = '#groups/edit?groupId='+groupId;
	})
	
	$(".more-actions .delete").click(function() {
		// delete user
		var groupId = $(this).closest('tr').data('group-id');
		
		var cnfm = confirm('Are you sure want to delete this group?');
		if (cnfm) {
			var grp_obj = {'groupId': groupId};
			FacilioApp.ajax({
				method : "post",
				url : contextPath + "/app/setup/groups/delete",
				data : grp_obj,
				done: function(data) {
					FacilioApp.notifyMessage('success', 'Group deleted successfully!');
					FacilioApp.refreshView();
				},
				fail: function(error) {
					alert(JSON.stringify(error.responseJSON.fieldErrors));
				}
			});
		}
	})
</script>