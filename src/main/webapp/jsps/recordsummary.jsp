<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/viewticket.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/checkbox.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/view.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/progressbar.css" rel="stylesheet">

<!-- Header -->
<div class="row form-header record-header" >
	<div class="col-sm-12" >
  	<h4 class="pull-left"><s:property value="%{record[recordSummaryLayout.titleColumnId]}" /></h4>
    <div class="action-btn text-right">
     		<button type="button"  class="btn btn-default edit" style=" border-radius: 100px;border-color: #50CA7C;color: #666666;" onclick="location.href='#';"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>&nbsp;&nbsp;Edit</button>
     		<button type="button" class="btn btn-default btn-circle cancel-btn" onclick="location.href='#<s:property value="%{moduleLinkName}" />';"><i class="fa fa-times"></i></button>
	</div>
  	</div>
</div>

<!--------------------------- progress bar ------------------------>
<div class="view-content record-content">
<s:if test="%{recordSummaryLayout.hasProgressBar}">
	<div class="row record-progress-content">
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
</s:if>
<div class="row  row-in-2x">
	<div class="col-lg-12 col-md-12 ticket-form">
		<div class="form-group">
			<s:if test="%{recordSummaryLayout.hasProgressBar}">
				<h3>
				<s:property value="%{record[recordSummaryLayout.recordTitleColumnId]}" />
				</h3>
				<div class="col-md-12 col-sm-12 hr-dashed-line"></div>
			</s:if>
			<div  style="padding-top:20px;">
				<table class="table table-borderless">
					<s:iterator var="column" value="recordSummaryLayout.columns">
						<tr>
							<th class="left-th"><s:property value="#column.label" /></th>
							<td><s:property value="record[#column.id]" /></td>
						</tr>
					</s:iterator>
				</table>
			</div>
		</div>
	</div>
</div>

<s:if test="%{!recordSummaryLayout.relatedModules.isEmpty()}">
	<div  style="padding-bottom:20px;">
	<s:iterator var="relatedModule" value="recordSummaryLayout.relatedModules">
	<div class="row row-out">
		<div class="row ">
			<div class="col-md-12 col-sm-12 row-header-out ">	
				<i class="<s:property value="#relatedModule.icon" /> icon-out-2x" aria-hidden="true"></i>&nbsp;&nbsp;<s:property value="#relatedModule.displayName" />
			</div>
		</div>
		<s:if test="record[#relatedModule.listName] == null || record[#relatedModule.listName].isEmpty()">
		<div class="row row-in">
			<div class="col-md-12 col-sm-12 form-group"><br></div>
			<div class="row row-footer">
				<div class="col-md-6 col-sm-6" onclick="FacilioApp.createRecordDialog('<s:property value="#relatedModule.linkName" />', relatedModuleAddCallBack, '<s:property value="%{moduleLinkName}" />', '<s:property value="%{record[recordSummaryLayout.pkColumnId]}" />');">
					<button type="button" class="btn btn-default  plus-btn"><i class="fa fa-plus"></i></button>&nbsp;&nbsp;New <s:property value="#relatedModule.moduleName" />
				</div>
			</div>
		</div>
		</s:if>
		<s:else>
		<div class="row row-in">
			<div class="row list-content table-style">
	   			<div>
					<table width="100%" class="table table-striped able-hover">
		    			<s:if test="#relatedModule.showHeader">
		    			<thead>
		        		<tr>
		        			<s:iterator var="column" value="#relatedModule.columns">
		       		 			<th><s:property value="#column.label" /></th>
		       		 		</s:iterator>
		        		</tr>
		      			</thead>  
		      			</s:if>
		      			<tbody>
		      				<s:iterator var="relatedrecord" value="record[#relatedModule.listName]">
		      				<tr>
		      					<s:iterator var="column" value="#relatedModule.columns">
		      					<s:if test="#column.showColumn">
		      						<td><s:property value="#relatedrecord[#column.id]" /></td>
		      					</s:if>	
		      					</s:iterator>
		      				</tr>
		      				</s:iterator>
		      			</tbody>
					</table>
				</div>	
				<div class="col-md-12 col-sm-12 hr-dashed-line"></div>
				<div class="row row-footer">
					<div class="col-md-6 col-sm-6" onclick="FacilioApp.createRecordDialog('<s:property value="#relatedModule.linkName" />', relatedModuleAddCallBack, '<s:property value="moduleLinkName" />', '<s:property value="%{record[recordSummaryLayout.pkColumnId]}" />');">
						<button type="button" class="btn btn-default  plus-btn"><i class="fa fa-plus"></i></button>&nbsp;&nbsp;New <s:property value="#relatedModule.moduleName" />
					</div>
				</div>
			</div>
		</div>
		</s:else>
	</div>
	</s:iterator>
	</div>
</s:if>
</div>



<script>

var relatedModuleAddCallBack = function(result, error)
	{
	   if (result != null) {
		   FacilioApp.notifyMessage('success', 'Record created successfully!');
		   
		   setTimeout(function() {
			   $('#record-list').find('tr.selected').trigger("click");
		   }, 500);
	   }
	}

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
 var currentStage = 'Awaiting';
 var lastStage = ["Closed"];
 


 for (var i=0; i< stages.length; i++) {
	 var stage = stages[i];
	 if((currentStage == null) | (currentStage == '')  ){
		 break;
	 }
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




