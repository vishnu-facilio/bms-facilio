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
				
		$('.sidebar .sidebar-footer-btn').click(function() {
			if ($('.sidebar').hasClass('fc-navbar-collapse')) {
				$('.sidebar').removeClass('fc-navbar-collapse');
				$('#page-wrapper').css('margin', '0 0 0 220px');
				
			}
			else {
				$('.sidebar').addClass('fc-navbar-collapse');
				$('#page-wrapper').css('margin', '0 0 0 50px');
			}
		});
		
		
		$(document).on('ajaxStart', function() { NProgress.start(); });
		$(document).on('ajaxStop',   function() { NProgress.done();  });
	},
	
	initSetup: function() {
		var self = this;
		
		$('.sidebar').perfectScrollbar();    
		
		if (location.hash.trim() == "") {
			location.hash = '#mysettings';
		}
		self.loadUrlFromHash(true);
		$(window).on('hashchange',function(){ 
			self.loadUrlFromHash(true);
		});
		
		$(document).on('ajaxStart', function() { NProgress.start(); });
		$(document).on('ajaxStop',   function() { NProgress.done();  });
	},
	
	loadUrlFromHash: function(is_setup) {
		var hashVal = location.hash;
		if ($('.sidebar ul li a[href="'+hashVal+'"]').length > 0) {
			$('.sidebar ul li a').removeClass('active');
			$('.sidebar ul li a[href="'+location.hash+'"]').addClass('active');
		}

		var module = location.hash.slice(1);
		if(module != "")
		{	
			var url = contextPath + '/home/' + module;
			if (is_setup) {
				url = contextPath + '/home/setup/' + module;
			}
			$('#page-wrapper').load(url, function(response, status, xhr) {
				if (response.indexOf("Struts Problem Report") > 0) {
					$('#page-wrapper').html("<div style='text-align:center;padding-top: 56px;'><i class='fa fa-suitcase fa-5' aria-hidden='true' style='font-size: 4em;'></i><h2>Work in progress.</h2></div>");
				}
				else {
					if ($(this).find('.form-content').length > 0) {
						$(this).find('.form-content').perfectScrollbar({
					
						});
					}
					else if ($(this).find('.view-content').length > 0) {
						$(this).find('.view-content').perfectScrollbar({
							
						});

					}
					else if ($(this).find('.temp-view-content').length > 0) {
						$(this).find('.temp-view-content').perfectScrollbar(
								 {}	
						);

					}
					else if ($(this).find('.list-content').length > 0) {
						   $('.list-content').perfectScrollbar({
								 suppressScrollX : true
							 });

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
	
	lookupDialog: function(moduleLinkName, moduleName, fieldId) {
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
		var $select = $("select[name='"+$(popup).data('fieldId')+"']").selectize();
		var selectize = $select[0].selectize;
		selectize.addOption({value: id, text: label});
		selectize.refreshOptions();
		selectize.setValue(id);
		$(popup).modal('hide');
	},
	
	createRecordDialog: function(moduleLinkName, callback, parentModuleLinkName, parentId) {
		 $.ajax({
			type : "GET",
			url : contextPath + '/home/'+moduleLinkName+'/newDialog',
			success : function(response) {
				$('#newRecordModel').html(response);
				if(parentModuleLinkName != undefined)
				{
					$('#addFormDialog').find('input[name='+moduleLinkName+'\\.parentModuleLinkName]').val(parentModuleLinkName);
					$('#addFormDialog').find('input[name='+moduleLinkName+'\\.parentId]').val(parentId);
				}
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
							url : contextPath + '/home/'+moduleLinkName+'/add',
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
	},
	
	userPickList: function(selector, options) {
		/*
		 *  options: {
		 *  	all: true, --> return's all users from the organization
		 *  	assign: <module>, --> return's assignable users for module (work order)
		 *  	group: <group_id>, --> return's group members
		 *  	default_value: [] --> default values
		 *  }
		 */
		
		var ajaxURL = '/home/users';
		if (options.group) {
			ajaxURL = '/home/users?groupId='+options.group;
		}
		 $.ajax({
				type : "GET",
				url : contextPath + ajaxURL,
				success : function(response) {
					
					var REGEX_EMAIL = '([a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*@' +
			        '(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)';

					var selObj = $(selector).selectize({
						persist: false,
						maxItems: null,
						valueField: 'orgUserId',
						labelField: 'name',
						searchField: ['name', 'email'],
						options: response,
						render: {
							item: function(item, escape) {
								return '<div>' +
								(item.name ? '<span class="name">' + escape(item.name) + '</span>' : '') +
								(item.email ? '<span class="email">' + escape(item.email) + '</span>' : '') +
								'</div>';
							},
							option: function(item, escape) {
								var label = item.name || item.email;
								var caption = item.name ? item.email : null;
								return '<div>' +
								'<span class="label1">' + escape(label) + '</span>' +
								(caption ? '<span class="caption">' + escape(caption) + '</span>' : '') +
								'</div>';
							}
						},
						createFilter: function(input) {
							var match, regex;

							// email@address.com
							regex = new RegExp('^' + REGEX_EMAIL + '$', 'i');
							match = input.match(regex);
							if (match) return !this.options.hasOwnProperty(match[0]);

							// name <email@address.com>
							regex = new RegExp('^([^<]*)\<' + REGEX_EMAIL + '\>$', 'i');
							match = input.match(regex);
							if (match) return !this.options.hasOwnProperty(match[2]);

							return false;
						},
						create: function(input) {
							if ((new RegExp('^' + REGEX_EMAIL + '$', 'i')).test(input)) {
								return {email: input};
							}
							var match = input.match(new RegExp('^([^<]*)\<' + REGEX_EMAIL + '\>$', 'i'));
							if (match) {
								return {
									email : match[2],
									name  : $.trim(match[1])
								};
							}
							alert('Invalid email address.');
							return false;
						}
					});
					if (options.default_value) {
						selObj[0].selectize.setValue(options.default_value);
					}
				}
		 });
	}
};