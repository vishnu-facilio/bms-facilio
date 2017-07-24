/**
 * 
 */
FacilioApp = {

	init: function() {
		var self = this;
		
		$('.sidebar').perfectScrollbar();    
		
		if (location.hash.trim() == "") {
			location.hash = '#dashboard';
		}
		self.loadUrlFromHash();
		$(window).on('hashchange',function(){ 
			self.loadUrlFromHash();
		});
		
		$('.sidebar ul li a').click(function(e) {
			e.preventDefault();
			console.log('clicked..');
			var href = $(this).attr("href");
			console.log('clicked.. '+href);
			location.href = href;
		});
		
		$(document).on('ajaxStart', function() { NProgress.start(); });
		$(document).on('ajaxStop',   function() { NProgress.done();  });
	},
	
	loadUrlFromHash: function() {
		var hashVal = location.hash;
		if ($('.sidebar ul li a[href="'+hashVal+'"]').length > 0) {
			$('.sidebar ul li a').removeClass('active');
			$('.sidebar ul li a[href="'+location.hash+'"]').addClass('active');
		}

		var module = location.hash.slice(1);
		if(module != "")
		{	
			var url = contextPath + '/home/' + module;
			$('#page-wrapper').load(url, function(response, status, xhr) {
				if (response.indexOf("Struts Problem Report") > 0) {
					$('#page-wrapper').html("<div style='text-align:center;padding-top: 56px;'><i class='fa fa-suitcase fa-5' aria-hidden='true' style='font-size: 4em;'></i><h2>Work in progress.</h2></div>");
				}
				else {
					if ($(this).find('.form-content').length > 0) {
						$(this).find('.form-content').perfectScrollbar();
					}
					else if ($(this).find('.view-content').length > 0) {
						$(this).find('.view-content').perfectScrollbar();

					}
					else if ($(this).find('.temp-view-content').length > 0) {
						$(this).find('.temp-view-content').perfectScrollbar();

					}
				}
			});
		}
	},
	
	refreshView: function() {
		this.loadUrlFromHash();
	},
	
	notifyMessage: function(action, message) {
		$('.common-notification-alert .alert').removeClass('alert-success').removeClass('alert-info').removeClass('alert-warning').removeClass('alert-danger');
		$('.common-notification-alert .alert').addClass('alert-'+action);
		$('.common-notification-alert .alert .msg').text(message);
		
		$(".common-notification-alert .alert").show();
		$(".common-notification-alert .alert").delay(2000).slideUp(200);
	},
	
	lookupDialog: function(moduleLinkName, moduleName, criteria, fieldId) {
		var headerHtml = '<div id="'+moduleLinkName+'popup" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="helpModalLabel" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><h4 class="modal-title pull-left"></h4><div class="action-btn text-right"><button type="button" class="btn btn-default btn-circle cancel-btn" data-dismiss="modal"><i class="fa fa-times"></i></button>';
		if(moduleLinkName != 'area')
		{
			headerHtml = headerHtml + '<button type="button" class="btn btn-default save-btn"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;New</button>';
		}	
		headerHtml = headerHtml + '</div></div><div class="modal-body"></div></div></div></div>';
		
		var popup = $(headerHtml);
		$(popup).find('.modal-body').html("Loading...");
		$(popup).find('.modal-title').text("All " + moduleName);
		$(popup).data('fieldId', fieldId);
		$(popup).modal("show");
		$.ajax({
			method : "get",
			url : contextPath + "/home/" + moduleLinkName + "/popup",
		})
		.done(function(data) {
			$(popup).find('.modal-body').html(data);
		});
		$(popup).on('hidden.bs.modal', function () {
			$(popup).remove();
		})
	},
	
	selectValue: function(id, label, popup) {
		var $select = $("#"+$(popup).data('fieldId')).selectize();
		var selectize = $select[0].selectize;
		selectize.addOption({value: id, text: label});
		selectize.refreshOptions();
		selectize.setValue(id);
		$(popup).modal('hide');
	},
	
	createRecordDialog: function(moduleName, callback) {
		 $.ajax({
			type : "GET",
			url : contextPath + '/home/'+moduleName+'/newDialog',
			success : function(response) {
				$('#newRecordModel').html(response);
				$("#newRecordModel").modal("show");
				
				// handle save record on dialog
				$('#addFormDialog').validator().on('submit', function (e) {
				  if (e.isDefaultPrevented()) {
						// handle the invalid form...
				  }
				  else {
						// check if any validation errors
						if ($(this).find('.form-group').hasClass('has-error')) {
							return false;
						}
						
						$(this).find(".save-btn").button('loading');
						$.ajax({
							method : "post",
							url : contextPath + '/home/'+moduleName+'/add',
							data : $("#addFormDialog").serialize()
						})
						.done(function(data) {
							$('#newRecordModel').modal('hide');
							callback(data, null);
						})
						.fail(function(error) {
							$(".save-btn").button('reset');
							callback(null, error);
						});
						return false;
				  	}
				});
			}
		});
	}
};

$(document).ready(function() {
	FacilioApp.init();
});