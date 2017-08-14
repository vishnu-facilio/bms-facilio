<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div class="form-container form-content">
<form role="form" id="addLocationForm" onsubmit="return false;">
	<div class="row">
		<div class="col-lg-6">
			<div class="panel-body">
				<div class="form-group">
					<label>Name</label>
					<s:textfield name="location.name"
						id="inputName" class="form-control"/>
				</div>
				<div class="form-group">
					<label>Street</label>
					<s:textfield name="location.street"
						id="inputStreet" class="form-control"/>
				</div>
				<div class="form-group">
					<label>City</label>
					<s:textfield name="location.city"
						id="inputCity" class="form-control"/>
				</div>
				<div class="form-group">
					<label>State / Province</label>
					<s:textfield name="location.state"
						id="inputState" class="form-control"/>
				</div>
				<div class="form-group">
					<label>Zip / Postal Code</label>
					<s:textfield name="location.zip"
						id="inputZip" class="form-control"/>
				</div>
				<div class="form-group">
					<label>Country</label>
					<s:textfield name="location.country"
						id="inputCountry" class="form-control"/>
				</div>
				<div class="form-group">
					<label>Latitude & Longitude</label>
					<div class="input-group">
						<s:textfield name="location.lat" id="inputLat" class="form-control" placeholder="Lat"/>
					    <span class="input-group-addon">&</span>
					    <s:textfield name="location.lng" id="inputLng" class="form-control" placeholder="Lng"/>
					</div>
				</div>
			</div>
		</div>
		<div class="col-lg-6">
			<div class="panel-body">
				<div class='map-wrapper'>
					<div class="form-group">
						<label>Find / Pick address from Maps</label>
						<div class="input-group">
							<input type="text" name="ticket.requester" value="" id="inputAddress" class="form-control ui-autocomplete-input" placeholder="Find an address">
							<span class="input-group-btn">
								<button class="btn btn-default btn-md" type="button" style="padding:9px 12px;">
									<i class="fa fa-search"></i>
								</button>
							</span>
	                    </div>
	               </div>
			      <div id="map" style="border: 1px solid #DDD; height: 300px; margin: 20px 0 10px 0;"></div>
			      <div id="legend">You can drag and drop the marker to the correct location</div>
			    </div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12"><hr></div>
		<div class="col-lg-6">
			<div class="panel-body">
				<div class="form-group">
					<label>Contact</label>
					<s:select class="form-control" list="actionForm.userList"
						name="location.contact" id="inputContact" headerKey="0"
						headerValue="--" />
				</div>
				<div class="form-group">
					<label>Phone</label>
					<s:textfield class="form-control" name="location.phone" id="inputPhone" />
				</div>
			</div>
		</div>
		<div class="col-lg-6">
			<div class="panel-body">
				<div class="form-group">
					<label>Fax Phone</label>
					<s:textfield class="form-control" name="location.faxPhone" id="inputFaxPhone" />
				</div>
			</div>
		</div>
	</div>
	<!-- div class="row">
		<div class="col-lg-12">
			<div class="panel-body">
				<div class="form-group">
					<button type="reset" class="btn btn-outline btn-default"
						onclick="location.href='#locations';">Go Back</button>
					<button type="submit" data-loading-text="<i class='fa fa-spinner fa-spin '></i> Saving" onclick="saveLocation(this);" class="btn btn-primary">Save</button>
				</div>

			</div>
		</div>
	</div -->
</form>
</div>
<script>
$('#addLocationForm').tooltip({
    selector: "[data-toggle=tooltip]",
    container: "body"
});

	$(document).ready(function() {
		
		$(".action-btn .save-btn").click(function() {
			$('#addLocationForm').submit();
		});
		
		$(".action-btn .cancel-btn").click(function() {
			location.href = '#locations';
		});
		
		var addresspickerMap = $( "#inputAddress" ).addresspicker({
		      updateCallback: updateAddressInForm,
		      mapOptions: {
		        zoom: 5,
		        center: new google.maps.LatLng(46, 2),
		        scrollwheel: false,
		        mapTypeId: google.maps.MapTypeId.ROADMAP
		      },
		      elements: {
		        map:      "#map"
		      }
		    });
		
			$("#inputAddress").addresspicker("option", "reverseGeocode", true);

		    var gmarker = addresspickerMap.addresspicker( "marker");
		    gmarker.setVisible(true);
		    addresspickerMap.addresspicker( "updatePosition");

		    function updateAddressInForm(geocodeResult, parsedGeocodeResult) {
		    	console.log(geocodeResult);
		    	console.log(parsedGeocodeResult);
		    	
		    	$("#inputName").val(geocodeResult.formatted_address);
		    	
		    	if (parsedGeocodeResult.street_number) {
		    		$("#inputStreet").val(parsedGeocodeResult.street_number);
		    	}
		    	if (parsedGeocodeResult.route) {
		    		var street = ($("#inputStreet").val().trim() == "") ? parsedGeocodeResult.route : $("#inputStreet").val() + ', ' + parsedGeocodeResult.route; 
		    		$("#inputStreet").val(street);
		    	}
		    	if (parsedGeocodeResult.locality) { 
		    		$("#inputCity").val(parsedGeocodeResult.locality);
		    	}
		    	if (parsedGeocodeResult.administrative_area_level_1) { 
		    		$("#inputState").val(parsedGeocodeResult.administrative_area_level_1);
		    	}
		    	if (parsedGeocodeResult.postal_code) { 
		    		$("#inputZip").val(parsedGeocodeResult.postal_code);
		    	}
		    	if (parsedGeocodeResult.country) { 
		    		$("#inputCountry").val(parsedGeocodeResult.country);
		    	}
		    	
		    	if (typeof(parsedGeocodeResult.lat) === 'function') {
		    		$("#inputLat").val(parsedGeocodeResult.lat());
		    	}
		    	else {
		    		$("#inputLat").val(parsedGeocodeResult.lat);
		    	}
		    	if (typeof(parsedGeocodeResult.lng) === 'function') {
		    		$("#inputLng").val(parsedGeocodeResult.lng());
		    	}
		    	else {
		    		$("#inputLng").val(parsedGeocodeResult.lng);
		    	}
		    }
		
		$("#addLocationForm").submit(function() {
			$("#addLocationForm button[type=submit]").button('loading');
			
			FacilioApp.ajax({
				method : "post",
				url : "<s:url action='add' />",
				data : $("#addLocationForm").serialize(),
				done: function(data) {
					console.log(data);
					window.location.href='#locations';
					FacilioApp.notifyMessage('success', 'Location created successfully!');
				},
				fail: function(error) {
					console.log(error);
				} 
			});
		});
	});
</script>