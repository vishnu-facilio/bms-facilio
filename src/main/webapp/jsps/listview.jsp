<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>    

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/checkbox.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/view.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery.perfect-scrollbar/0.7.0/css/perfect-scrollbar.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.perfect-scrollbar/0.7.0/js/perfect-scrollbar.jquery.min.js"></script>

<div class="col-md-12 col-lg-12 record-list">
<div class="row form-header" >
<div class="col-sm-12" >
		            	
		            	<h4 class="pull-left" style="cursor:pointer" data-toggle="dropdown"><s:property value="%{viewDisplayName}" />
		            	
   						 <span class="caret"></span></h4>
   						 
			<s:if
				test="%{moduleLinkName == 'ticket'}">

				<ul class="dropdown-menu" role="menu">
					<f:hasPermission permission="WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,WORKORDER_ACCESS_READ_ANY">
						<li><a href="#ticket">All Work Orders</a></li>
						<li><a href="#ticket/allopentickets">All Open Work Orders</a></li>
						<li><a href="#ticket/overduetickets">Overdue Work Orders</a></li>
					</f:hasPermission>
					<f:hasPermission permission="WORKORDER_ACCESS_READ_OWN,WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,WORKORDER_ACCESS_READ_ANY">
						<li><a href="#ticket/mytickets">My Work Orders</a></li>
						<li><a href="#ticket/myopentickets">My Open Work Orders</a></li>
						<li><a href="#ticket/myoverduetickets">My Overdue Work Orders</a></li>
					</f:hasPermission>
				</ul>
				
				<f:hasPermission permission="WORKORDER_CREATE">
					<div class="action-btn text-right">
				 		<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
					 </div>
				</f:hasPermission>
			</s:if>
			<s:elseif
				test="%{moduleLinkName == 'task'}">

				<ul class="dropdown-menu" role="menu">
					<f:hasPermission permission="TASK_ACCESS_READ_ANY">
						<li><a href="#task">All Tasks</a></li>
					</f:hasPermission>
					<f:hasPermission permission="TASK_ACCESS_READ_OWN, TASK_ACCESS_READ_ANY">
						<li><a href="#task/mytasks">My Tasks</a></li>
					</f:hasPermission>
				</ul>
				
				<f:hasPermission permission="TASK_CREATE">
					<div class="action-btn text-right">
				 		<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
					 </div>
				</f:hasPermission>
			</s:elseif>
			<s:elseif
				test="%{moduleLinkName == 'campus' || moduleLinkName == 'building'}">
					<f:hasPermission permission="SPACEMANAGEMENT_ACCESS_ENABLE">
						<div class="action-btn text-right">
					 		<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
					 		<button type="button" class="btn btn-default import-btn save-btn">Import</button>
						 </div>
					</f:hasPermission>
			</s:elseif>
  </div>
</div>
<s:if test="%{records == null || records.isEmpty()}">
	<div class="row content-center">
		<div class="col-lg-12 col-md-12 text-center ">
			<img class="center-block" src="${pageContext.request.contextPath}/images/noworkorder.svg" />
	     	<div>&nbsp;</div>
			<div class="no-screen-msg"><div class="row-title text-bold">No record created yet ...</div><div class="row-subtitle">Since you have not created any records,</div><div class="row-subtitle">Why not create a new one?</div></div>
	 		<div class="action-btn text-center">
			<div>&nbsp;</div>
			<s:if test="%{moduleLinkName == 'ticket'}">
				<f:hasPermission permission="WORKORDER_CREATE">
					<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
				</f:hasPermission>
			</s:if>
			<s:elseif test="%{moduleLinkName == 'task'}">
				<f:hasPermission permission="TASK_CREATE">
					<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
				</f:hasPermission>
			</s:elseif>
			</div>
		</div>
	</div>
</s:if>
<s:else>
<div class="row list-content">
   <div class="fc-list-table col-md-12 col-sm-12 ">
	<table width="100%" class="table table-striped able-hover " id="record-list">
	    <thead>
	        <tr>
	       		<th class="dt-body-center">
	       			<div class="checkbox checkbox-primary">
	       		 	 	<input type="checkbox" name="select_all" id="record-select-all">
	       		 	 	<label for="record-select-all"></label>
	       		 	</div>
	       		</th>
	       		<s:iterator var="column" value="viewlayout.columns">
	       			<th><s:property value="#column.label" /></th>
	       		</s:iterator>
	            <th class="sorting_disabled"></th>
	        </tr>
	    </thead>
	    <tbody>
	    	<s:iterator var="record" value="records">
				<tr class="odd gradeX">
					<td></td>
					<s:iterator var="column" value="viewlayout.columns">
						<td>
							<input type="hidden" class="summary-url" value="<s:property value="%{moduleLinkName}" />/summary/<s:property value="#record[viewlayout.pkColumnId]" />">
							<s:if test="%{#column.columnType == @com.facilio.bmsconsole.context.Column$ColumnType@MULTICOLUMN}">
								<div class="row-title"><s:property value="#record[#column.columns.get(0).id]" /></div>
		            			<div class="row-subtitle"><s:property value="#record[#column.columns.get(1).id]" /></div>
							</s:if>
							<s:elseif test="%{#column.columnType == @com.facilio.bmsconsole.context.Column$ColumnType@DATETIME}">
								<s:date name="#record[#column.id]" format="dd/MM/yyyy hh:mm" />
							</s:elseif>
							<s:elseif test="%{#column.columnType == @com.facilio.bmsconsole.context.Column$ColumnType@LOOKUP}">
								<s:property value="#record[#column.id][#column.lookupId]" />
							</s:elseif>
							<s:else>
								<s:property value="#record[#column.id]" />
							</s:else>
						</td>
	    			</s:iterator>
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
</div>
<div class="col-md-8 col-lg-8 record-summary" style="display:none"></div>

<!-- new record popup -->
<div class="modal fade" id="newRecordModel" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true"></div>

<script>
 $(document).ready(function (){
	 var table = $('#record-list').DataTable({ 
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
	         targets: '<s:property value="viewlayout.columns.size() + 1" />',
	         searchable: false,
	         orderable: false
	      },
	      {
	         targets: 1,
	         orderable: false
	      }],
	      order: [[1, 'asc']],
	      language: {
	    	  paginate: {
	    		  previous: "<",
	    		  next: ">"
	    	  }
	      },
	      
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      lengthChange: false,
	    /*scrollY : "200px"*/
	      
	   });

	   // Handle click on "Select all" control
	   $('#record-select-all').on('click', function(){
	      // Get all rows with search applied
	      var rows = table.rows({ 'search': 'applied' }).nodes();
	      // Check/uncheck checkboxes for all rows in the table
	      $('input[type="checkbox"]', rows).prop('checked', this.checked);
	   });

	   // Handle click on checkbox to set state of "Select all" control
	   $('#record-list tbody').on('change', 'input[type="checkbox"]', function(){
	      // If checkbox is not checked
	      if(!this.checked){
	         var el = $('#record-select-all').get(0);
	         el.checked = false;
	      }
	   });

	   var hideCols = null;
	   $('#record-list tbody tr').click(function(e) {
		   
		   $('.dataTable tbody tr').removeClass('selected');   
		   $(this).addClass('selected');
		   var summaryURL = $(this).find('.summary-url').val();
		  
		   var screenWidth = $( window ).width();
		   console.log(screenWidth);
		   if (screenWidth < 600) {
			   // Mobile view -> In mobile phones, we will preview record in full view
			   location.href = '#' + summaryURL;
		   }
		   else {
			   // Web view
			   var url = contextPath + '/app/' + summaryURL;
			   $('.record-summary').load(url, function(response, status, xhr) {
				   
				   showRecordPreview();
				   
				   $('.record-summary .cancel-btn').click(function(e) {
					   e.preventDefault();
					   
					   hideRecordPreview();
					   $('.dataTable tbody tr').removeClass('selected');
				   });
				   
				   var ht = $( window ).height();
				   $('.record-list').css('height', (ht-70)+'px');
				   $('.record-summary .view-content').css('height', (ht-60)+'px');
				   $('.record-summary .view-content').css('padding-bottom', '60px');
				   $('.record-summary .view-content').perfectScrollbar();
			   });
		   }
	   });
	   $(".import-btn").click(function() {
		   var moduleLinkName = '<s:property value="%{moduleLinkName}" />';
		   //#import/showformupload?module=campus
		   location.href = '#import/showformupload?module=<s:property value="%{moduleLinkName}" />';
	   });
	   
	   $(".new-btn").click(function() {
		   var moduleLinkName = '<s:property value="%{moduleLinkName}" />';
		   var actionType = '<s:property value="%{newActionType}"/>';
		   
		   if (actionType === 'dialog') {
			   
			   FacilioApp.createRecordDialog(moduleLinkName, function(result, error) {
				   if (result != null) {
					   FacilioApp.notifyMessage('success', 'Record created successfully!');
					   
					   setTimeout(function() {
						   FacilioApp.refreshView();
					   }, 500);
				   }
			   });
		   }
		   else {
			   location.href = '#<s:property value="%{moduleLinkName}" />/new';
		   }
		});
	   
	   var showRecordPreview = function() {
		   if(!$('.record-summary').is(':visible')) {
			   if (table.columns()[0].length > 3) {
				   hideCols = table.columns()[0].slice(3);
			   }
			   else {
				   hideCols = [];
			   }
			   for (var i=0; i< hideCols.length; i++) {
				   var col = table.column(hideCols[i]);
				   col.visible(false);
			   }
			   
			   $('.record-list').removeClass('col-md-12').removeClass('col-lg-12').addClass('col-md-4').addClass('col-lg-4');
			   $('.record-summary').show();
		   }
	   };
	   
	   var hideRecordPreview = function() {
		   if($('.record-summary').is(':visible')) {
			   if (table.columns()[0].length > 3) {
				   hideCols = table.columns()[0].slice(3);
			   }
			   else {
				   hideCols = [];
			   }
			   for (var i=0; i< hideCols.length; i++) {
				   var col = table.column(hideCols[i]);
				   col.visible(true);
			   }
			   
			   $('.record-list').removeClass('col-md-4').removeClass('col-lg-4').addClass('col-md-12').addClass('col-lg-12');
			   $('.record-summary').hide();
		   }
	   };
	   
	 
	});
 
 	
</script>
