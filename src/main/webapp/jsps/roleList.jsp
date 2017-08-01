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
            <th></th>
        </tr>
    </thead>
    <tbody>
    	<s:iterator var="role" value="%{roles}">
			<tr class="odd gradeX" data-role-id="<s:property value="#role.roleId" />">
  					<td>
                       <span class="user-name"><s:property value="#role.name" /></span>
                   </td>
                   <td>
                   	<s:property value="#role.description" />
                   </td>
                   <td>Jul 28, 2017</td>
                   <td class="center">
                   		<div class="dropdown more-actions">
	                   		<a data-toggle="dropdown" class="dropdown-toggle"><i class="fa fa-ellipsis-h"></i></a>
	                   		<ul class="dropdown-menu" role="menu">
	                           	<li><a class="edit">Edit</a>
	                           	<li><a class="clone">Clone</a>
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
		var $userName = $('.user-name');
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
	      }],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      paging: false,
	      lengthChange: false,
	});
	
	$(".action-btn .new-btn").click(function() {
		location.href = '#roles/new';
	});
	
	$(".more-actions .edit").click(function() {
		// edit role
		var roleId = $(this).closest('tr').data('role-id');
		location.href = '#roles/edit?roleId='+roleId;
	})
	
	$(".more-actions .delete").click(function() {
		// delete role
		var roleId = $(this).closest('tr').data('role-id');
		
		var cnfm = confirm('Are you sure want to delete this role?');
		if (cnfm) {
			var grp_obj = {'roleId': roleId};
			FacilioApp.ajax({
				method : "post",
				url : contextPath + "/app/setup/roles/delete",
				data : grp_obj,
				done: function(data) {
					FacilioApp.notifyMessage('success', 'Role deleted successfully!');
					FacilioApp.refreshView();
				},
				fail: function(error) {
					alert(JSON.stringify(error.responseJSON.fieldErrors));
				}
			});
		}
	})
</script>