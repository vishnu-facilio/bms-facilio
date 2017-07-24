<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/viewticket.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/checkbox.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/view.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/progressbar.css" rel="stylesheet">



<!-- Header -->
<div class="row form-header" >
<div class="col-sm-12" >
  <h4 class="pull-left"><s:property value="ticket.ticketId" /></h4>
 	
    <div class="action-btn text-right">
     		<button type="button"  class="btn btn-default edit" style=" border-radius: 100px;border-color: #50CA7C;color: #666666;" onclick="location.href='#';"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;&nbsp;Edit</button>
     		<button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='#ticket';"><i class="fa fa-times"></i></button>
	 </div>
  </div>
</div>

<!--------------------------- progress bar ------------------------>
<div class="view-content">
<div class=" row">
  <div class=" col-lg-12 container">
            <div class="row bs-wizard" style="border-bottom:0;">
                
                <div class="col-xs-2 bs-wizard-step disabled">
                 <div class="progress"><div class="progress-bar"></div></div>
                  <a href="#" class="bs-wizard-dot"></a>
                  <div class="bs-wizard-info text-center">Open</div>
                </div>
                
                <div class="col-xs-2 bs-wizard-step disabled"><!-- complete -->
                  <div class="progress"><div class="progress-bar"></div></div>
                  <a href="#" class="bs-wizard-dot"></a>
                  <div class="bs-wizard-info text-center">Awaiting for approval</div>
                </div>
                
                <div class="col-xs-2 bs-wizard-step disabled"><!-- complete -->
                  <div class="progress"><div class="progress-bar"></div></div>
                  <a href="#" class="bs-wizard-dot"></a>
                  <div class="bs-wizard-info text-center">Approved</div>
                </div>
                
                <div class="col-xs-2 bs-wizard-step disabled"><!-- active -->
                  <div class="progress"><div class="progress-bar"></div></div>
                  <a href="#" class="bs-wizard-dot"></a>
                  <div class="bs-wizard-info text-center"> WIP</div>
                </div>
            
      
            <div class="col-xs-2 bs-wizard-step disabled">
                  <div class="progress"><div class="progress-bar"></div></div>
                  <a href="#" class="bs-wizard-dot"></a>
                  <div class="bs-wizard-info text-center">Closed</div>
                </div> 
            </div> 
         
          </div>  
</div>


<!-- ----------------------------------------------------- -->

<div class="row  row-in-2x">
<div class="col-lg-12 col-md-12 ticket-form">

<div class="form-group"><h3>
<s:property value="ticket.subject" />
</h3>
<div class="col-md-12 col-sm-12 hr-dashed-line">			
</div>
<table class="table table-borderless">
	<tr>
		<th class="left-th">Description</th>
		<td><s:property value="ticket.description" /></td>
	</tr>
	<tr>
		<th class="left-th">Priority</th>
		<td><s:property value="ticket.priority" /></td>
	</tr>
	<tr>
		<th class="left-th">Requested by</th>
		<td><s:property value="ticket.requester" /></td>
	</tr>
	<tr>
		<th class="left-th">Assigned to</th>
		<td><s:property value="ticket.agentId" /></td>
	</tr>
	<tr>
		<th class="left-th">Opened Date</th>
		<td><s:property value="ticket.openedDate" /></td>
	</tr>
	<tr>
		<th class="left-th">Due Date</th>
		<td><s:property value="ticket.dueDate" /></td>	</tr>
	<tr>
		<th class="left-th">Category</th>
		<td><s:property value="ticket.category" /></td>
	</tr>
	<tr>
		<th class="left-th">Location</th>
		<td><s:property value="ticket.location" /></td>
	</tr>
	<tr>
		<th class="left-th">Asset</th>
		<td><s:property value="ticket.asset" /></td>
	</tr>
	
</table>
</div>
</div>
</div>

<!-- --------------------------------------------------------------------- -->
<div class="row row-out">
	<div class="row ">
	<div class="col-md-12 col-sm-12 row-header-out " style="text-center">	
	<i class="fa fa-tasks icon-out-2x" aria-hidden="true"></i>&nbsp;&nbsp;TASKS
	</div>
	</div>
<div class="row row-in">
<div class="row list-content table-style">
   <div class="col-md-12 col-sm-12">
	<table width="100%" class="table table-striped able-hover " id="tickets-list">
	    <thead>
	        <tr>
	       		 <th>Name</th>
	            <th>Status</th>
	            <th>Due Date</th>
	            <th>Task start time</th>
	            <th>Task end time</th>
	            
	        </tr>
	      </thead>  
	
	    
	    
	    <!-- 
	    <tbody>
	    	<s:iterator var="ticket" value="tickets">
	   				<tr class="odd gradeX" id="<s:property value="#ticket.ticketId" />">
		            <td><a href="#ticket/<s:property value="#ticket.ticketId" />#"><s:property value="#ticket.ticketId" /></a></td>
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
			        </tr>
			</s:iterator>
			</tbody>
			-->
		
		
	</table>
	
	<div class="col-md-12 col-sm-12 hr-dashed-line">			
	</div>
	<div class="row row-footer">
	<div class="col-md-6 col-sm-6">
	<button type="button" class="btn btn-default  plus-btn" onclick="location.href='#';"><i class="fa fa-plus"></i></button>&nbsp;&nbsp;New Task
	</div>
	</div>
	
  </div>
</div>
</div>
</div>
	<div class="row row-out">
	<div class="row ">
	<div class="col-md-12 col-sm-12 row-header-out " style="text-center">	
	<i class="fa fa-paperclip icon-out-2x" aria-hidden="true"></i>&nbsp;&nbsp;ATTACHMENTS
	</div>
	</div>
	
	<div class="row row-in">
	<div class="col-md-12 col-sm-12 form-group" style="text-center"><br>
	Mitsubishi_lift_technician_guide.pdf		
	</div>
	<div class="col-md-12 col-sm-12 hr-dashed-line">			
	</div>
	<div class="row row-footer">
	<div class="col-md-6 col-sm-6 ">
	<button type="button" class="btn btn-default  plus-btn" onclick="location.href='#';"><i class="fa fa-plus"></i></button>&nbsp;&nbsp;New file
	</div>
	</div>
	</div>
	</div>
	
	<div class="row row-out">
	<div class="row ">
	<div class="col-md-12 col-sm-12 row-header-out " style="text-center">	
	<i class="fa fa-sticky-note icon-out-2x" aria-hidden="true"></i>&nbsp;&nbsp;NOTES
	</div>
	</div>
	<div class="row row-in">
	<div class="col-md-12 col-sm-12 form-group" style="text-center"><br>
	</div>
	<div class="row row-footer">
	<div class="col-md-6 col-sm-6 ">
	<button type="button" class="btn btn-default  plus-btn" onclick="location.href='#';"><i class="fa fa-plus"></i></button>&nbsp;&nbsp;Add Notes
	</div>
	</div>
	</div>
	</div>
	<div class="row row-out">
	</div>
</div>



<script>
 $(document).ready(function (){
	 /*  var table = $('#tickets-list').DataTable({
	      columnDefs: [{
	         targets: 0,
	         searchable: false,
	         orderable: false,
	      },
	      {
	         targets: 6,
	         searchable: false,
	         orderable: false
	      },
	      {
		         targets: 5,
		         orderable: false
		  },
		  {
			     targets: 4,
			     orderable: false
		  },
		  {
		         targets: 3,
		         orderable: false
		   },
		   {
		         targets: 2,
		         orderable: false
		   },
	      {
	         targets: 1,
	         orderable: false
	      },
		   {
		         targets: 0,
		         orderable: true
		      }],
	      order: [[2, 'asc']],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      lengthChange: false,
	     
	   });

	 */
   
	});
 
 var i = 1;
 
 
 var stages = ['Open', 'Awaiting', 'Approved', 'WIP', 'Closed'];
 var currentStage = 'Approved';
 var lastStage = ["Closed"];

 for (var i=0; i< stages.length; i++) {
	 var stage = stages[i];
	 
	 $('.bs-wizard .bs-wizard-step:nth-of-type(' + i + ')').removeClass('active').addClass('complete ');
	 $('.bs-wizard .bs-wizard-step:nth-of-type(' + i + ') .bs-wizard-dot').html('<i class="fa fa-check" aria-hidden="true"></i>');

	   	   
	 $('.bs-wizard .bs-wizard-step:nth-of-type(' + (i+1) + ')').removeClass('disabled').addClass('active');
	 $('.bs-wizard .bs-wizard-step:nth-of-type(' + (i+1) + ') .bs-wizard-dot').html('<i class="fa fa-dot-circle-o" aria-hidden="true"></i>');
	 
	 if (lastStage.indexOf(stage) != -1) {
		 $('.bs-wizard .bs-wizard-step:nth-of-type(' + (i+1) + ')').removeClass('active').addClass('complete ');
		 $('.bs-wizard .bs-wizard-step:nth-of-type(' + (i+1) + ') .bs-wizard-dot').html('<i class="fa fa-check" aria-hidden="true"></i>');
   	 }
	 
	 if (currentStage == stage) {
		 break;
	 }
 }
 
</script>




