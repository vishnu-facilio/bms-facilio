<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<table width="100%" class="table table-striped able-hover" id="record-list">
    <thead>
        <tr>
        	<th>
        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        		Sequence
        	</th>
        	<th>Priority</th>
            <th></th>
        </tr>
    </thead>
     <tbody>
    	<s:iterator var="priority" value="%{priorities}">
    		<tr class="odd gradeX" data-role-id="<s:property value="#priority.id" />">
    			<td>
					<s:property value="#priority.sequenceNumber" />
				</td>
				<td>
					<s:property value="#priority.priority" />
				</td>
				<td class="center">
					<!-- div class="dropdown more-actions">
						<a data-toggle="dropdown" class="dropdown-toggle"><i class="fa fa-ellipsis-h"></i></a>
						<ul class="dropdown-menu" role="menu">
							<li><a class="edit">Edit</a>
							<li><a class="delete">Delete</a>
						</ul>
					</div-->
					<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
					<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
				</td>
			</tr>
    	</s:iterator>
    </tbody>
</table>

<script>

$(".setup-list-container table").dataTable({
     language: {
    	  paginate: {
    		  previous: "<",
    		  next: ">"
    	  }
      },
      
      columnDefs: [{
    	  targets: 2,
    	  orderable: false
      }],
      ordering: false,
      buttons: false,
      responsive: true,
      searching: false,
      paging: false,
      lengthChange: false
});
</script>