<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/checkbox.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/view.css" rel="stylesheet">


<div class="row form-header" >
<div class="col-sm-12" >
  <h4 class="pull-left">All Work Orders</h4>
 	
    <div class="action-btn text-right">
    	
 		<button type="button" class="btn btn-default save-btn"  onclick="location.href='#ticket/new';"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
	 </div>
  </div>
</div>
<s:if test="%{tickets.isEmpty()}">

       <div class="row content-center">
        
     <div class="col-lg-12 col-md-12 text-center ">
          
           <img class="center-block" src="${pageContext.request.contextPath}/images/noworkorder.svg">
           <div>&nbsp;</div>
           
                   <div class="no-screen-msg"><div class="row-title text-bold">No work order added yet ...</div><div class="row-subtitle">Since you have not created any workorders,</div><div class="row-subtitle">Why not create a new one?</div></div>
           		
           		<div class="action-btn text-center">
    			<div>&nbsp;</div>
 				<button type="button" data-loading-text="<i class='fa fa-plus fa-plus-1x '></i> Saving" class="btn btn-default save-btn" onclick="location.href='#ticket/new';"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
	 			</div>
         
     
      
</div>
</div>
</s:if>
<s:else>
<div class="row list-content">
   <div class="col-md-12 col-sm-12">
	<table width="100%" class="table table-striped able-hover " id="tickets-list">
	    <thead>
	        <tr>
	       		 <th class="dt-body-center">
	       		 	<div class="checkbox checkbox-primary">
	       		 	 	<input type="checkbox" name="select_all" id="ticket-select-all">
	       		 	 	<label for="ticket-select-all"></label>
	       		 	 </div>
	       		  </th>
	            <th>#</th>
	            <th>Subject & Description</th>
	            <th>Status</th>
	            <th>Priority</th>
	            <th>Due Date</th>
	             <th>Requested by</th>
	            <th>Assigned To</th>
	            <th class="sorting_disabled"></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="ticket" value="tickets">
				<tr class="odd gradeX" id="<s:property value="#ticket.ticketId" />">
					<td></td>
		            <td><a href="#ticket/<s:property value="#ticket.ticketId" />">#<s:property value="#ticket.ticketId" /></a></td>
		            <td>
		            	<div class="row-title"><s:property value="#ticket.subject" /></div>
		            	<div class="row-subtitle"><s:property value="#ticket.description" /></div>
		            </td>
		            <td>
		            	<span class="text"><s:property value="#ticket.getStatus()" /></span>
		            </td>
		            <td>
		            <span class="label label-success"><s:property value="" /></span>
		            </td>
		            <td><s:property value="#ticket.duedate" /></td>
		            <td><s:property value="#ticket.requester" /></td>
		            <td><s:property value="#ticket.agentId" /></td>
		            <td>
		            	<div class="btn-group">
                            <button type="button" class="btn btn-outline-primary btn-sm dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                <i class="fa fa-gear"></i> <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                            	<li><a href="#">Clone</a>
                            	<li><a href="#">Edit</a>
								<li><a href="#">Delete</a>
                            </ul>
                        </div>
		            </td>
		        </tr>
			</s:iterator>
		</tbody>
	</table>
  </div>
</div>
</s:else>

<script>
 $(document).ready(function (){
	   var table = $('#tickets-list').DataTable({
	      columnDefs: [{
	         targets: 0,
	         searchable: false,
	         orderable: false,
	         className: 'dt-body-center',
	         render: function (data, type, full, meta){
	             return ' <div class="checkbox checkbox-primary"><input type="checkbox" id="'+meta.row+'_cbox" name="id[]" value="' + $('<div/>').text(data).html() + '"><label for="'+meta.row+'_cbox"></label></div>';
	         }
	      },
	      {
	         targets: 8,
	         searchable: false,
	         orderable: false
	      },
	      {
	         targets: 1,
	         orderable: false
	      }],
	      order: [[2, 'asc']],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      lengthChange: false
	   });

	   // Handle click on "Select all" control
	   $('#ticket-select-all').on('click', function(){
	      // Get all rows with search applied
	      var rows = table.rows({ 'search': 'applied' }).nodes();
	      // Check/uncheck checkboxes for all rows in the table
	      $('input[type="checkbox"]', rows).prop('checked', this.checked);
	   });

	   // Handle click on checkbox to set state of "Select all" control
	   $('#tickets-list tbody').on('change', 'input[type="checkbox"]', function(){
	      // If checkbox is not checked
	      if(!this.checked){
	         var el = $('#ticket-select-all').get(0);
	         el.checked = false;
	      }
	   });

	});
</script>
