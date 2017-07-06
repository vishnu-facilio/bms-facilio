/**
 * 
 */
FacilioApp = {

	init: function() {
		var self = this;
		
		self.loadUrlFromHash();
		$(window).on('hashchange',function(){ 
			self.loadUrlFromHash();
		});
		
		$(document).on('ajaxStart', function() { NProgress.start(); });
		$(document).on('ajaxStop',   function() { NProgress.done();  });
	},
	
	loadUrlFromHash: function() {	
		var module = location.hash.slice(1);
		if(module != "")
		{	
			var url = contextPath + '/home/' + module;
			$('#page-wrapper').load(url, function(response, status, xhr) {
				if (response.indexOf("Struts Problem Report") > 0) {
					$('#page-wrapper').html("<div style='text-align:center;padding-top: 56px;'><i class='fa fa-suitcase fa-5' aria-hidden='true' style='font-size: 4em;'></i><h2>Work in progress.</h2></div>");
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
	}
};

$(document).ready(function() {
	FacilioApp.init();
});