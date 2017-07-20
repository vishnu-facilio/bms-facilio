<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/checkbox.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/view.css" rel="stylesheet">


<div class="row form-header" >
<div class="col-sm-12" >
  <h4 class="pull-left"><s:property value="%{viewDisplayName}" /></h4>
    <div class="action-btn text-right">
 		<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
	 </div>
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
			<button type="button" class="btn btn-default new-btn save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>
			</div>
		</div>
	</div>
</s:if>
<s:else>
<div class="row list-content">
   <div class="col-md-12 col-sm-12">
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
							<s:if test="#column.isPrimaryColumn">
								<a href="#<s:property value="%{moduleLinkName}" />/summary/<s:property value="#record[#column.id]" />"><s:property value="#record[#column.id]" /></a>
							</s:if>
							<s:elseif test="%{#column.columnType == @com.facilio.bmsconsole.context.Column$ColumnType@MULTICOLUMN}">
								<div class="row-title"><s:property value="#record[#column.columns.get(0).id]" /></div>
		            			<div class="row-subtitle"><s:property value="#record[#column.columns.get(1).id]" /></div>
							</s:elseif>
							<s:elseif test="%{#column.columnType == @com.facilio.bmsconsole.context.Column$ColumnType@DATETIME}">
								<s:date name="#record[#column.id]" format="dd/MM/yyyy hh:mm" />
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
	         targets: <s:property value="viewlayout.columns.size() + 1" />,
	         searchable: false,
	         orderable: false
	      },
	      {
	         targets: 1,
	         orderable: false
	      }],
	      order: [[1, 'asc']],
	      
	      buttons: false,
	      responsive: true,
	      searching: false,
	      lengthChange: false
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
	});
</script>
