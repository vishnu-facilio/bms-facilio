<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

<link href="${pageContext.request.contextPath}/css/leed.css" rel="stylesheet">

<div class="form-header">
<div class="row">
<div class="col-sm-12" >
  <h4 class="pull-left ld-content-header">Project Registration<br><br>
  <small>Project registration includes access to performance</small>
  </h4>
   
  </div>
</div>
</div>
<div class="form-content leed-form ">
<form>


 <div class="row">
     <label class="ld-content-header">Enter your facility's details</label>
 
 <div>
    <div class="col-sm-4">
    <div class="ld-panel">
    <label><b>Project Name</b></label>
    <div >
   <input class="ld-input-text form-control" type=text >
   </div>
   </div>
   <div class="ld-panel">
    <label>Project type</label>
      <div >
   <select  name="org.country" class="form-control ld-select">
   <option>1</option>
	</select>
   </div>
    </div>
    </div>
  <div class="col-sm-4 ">
 
    <div class="ld-panel">
    <label>Unit type</label>
    <div >
   <label class="radio-inline"><input type="radio" value="ip" name="optradio"><b>IP</b></label>
	<label class="radio-inline"><input type="radio" value="si" name="optradio"><b>SI</b></label>
   </div>
   </div>
   <div class="ld-panel">
    <label>Rating System (Optional)</label>
      <div >
   <input class="ld-input-text form-control" type=text >
   </div>
    </div>
    </div>
    

	
</div>
</div>

  <div class="row">
     <label class="ld-content-header">Enter your organization's details </label>
 
 <div>
    <div class="col-sm-4">
    <div class="ld-panel">
    <label>Owner Type</label>
    <div >
   <select  name="org.country" class="form-control ld-select">
   <option>1</option>
	</select>
   </div>
   </div>
   <div class="ld-panel">
    <label>Owner Email</label>
      <div >
      <input class="ld-input-text form-control" type=text >
   
	
   </div>
    </div>
    </div>
  <div class="col-sm-4">
 
    <div class="ld-panel">
    <label>Owner Organization</label>
           <div >
   <select  name="org.country" class="form-control ld-select">
   <option>1</option>
	</select>
   </div>

   </div>
   <div class="ld-panel">
    <label>Owner Country</label>
     <div >
      <div >
    <select id="select-country" name="org.country" class="form-control">
	</select>
   </div>
   </div>
    </div>
    </div>
   

	
</div>
</div>
 
   <div class="row">
     <label class="ld-content-header">Where is your project located at?</label>
     <div>
         <div class="col-sm-4">
    <div class="ld-panel">
    <label>Area</label>
    <div >
   <input class="ld-input-text form-control" type=text >
   </div>
   </div>
   </div>
 </div>
 </div>
 
  <div class="row"> 
 <div>
    <div class="col-sm-4">
  
   <div class="ld-panel">
    <label>Address</label>
      <div >
       <input class="ld-input-text form-control" type=text >

   </div>
    </div>
     <div class="ld-panel">
    <label>Country</label>
      <div >
      <div >
    <select id="country" name="org.country" class="form-control">
	</select>
   </div>
   </div>
    </div>
     <div class="ld-panel">
    <label>Zip code</label>
      <div >
       <input class="ld-input-text form-control" type=text >

   </div>
    </div>
    </div>
  <div class="col-sm-4">
 
   <div class="ld-panel">
    <label>City</label>
      <div >
   <input class="ld-input-text form-control" type=text >
   </div>
    </div>
     <div class="ld-panel">
    <label>State</label>
      <div >
       <select  name="org.country" class="form-control ld-select">
   <option>1</option>
	</select>
   </div>
    </div>
     <div class="ld-panel">
    <label>Geo location (Optional)</label>
      <div >
       <input class="ld-input-text form-control" type=text >

   </div>
    </div>
    </div>
  
	
</div>
</div>

 <div class="row">
 <div class="col-md-8 ld-panel">
 <input type="checkbox" name="vehicle" value="Bike">This project is private<br>
 
 </div>
 </div>

		<div class="row">
			<label class="ld-content-header">Urjanet credentials</label>

			<div>
				<div class="col-sm-4">
					<div class="ld-panel">
						<label>User name</label>
						<div>
							<input class="ld-input-text form-control" type=text>
						</div>
					</div>

				</div>
				<div class="col-sm-4">

					<div class="ld-panel">
						<label>Password</label> <input class="ld-input-text form-control"
							type=text>

					</div>


				</div>


			</div>
		</div>

 <div class="row">
 <div class="col-md-8 ld-panel jumbotron">
 <input type="checkbox" name="vehicle" value="Bike">I agree to terms and conditions of <a href="#">service agreement</a><br>
 
 </div>
 </div>
  <div class="row">
 <div class="col-md-8 ld-panel ">
 <button type="button" class="btn btn-default pull-right ld-submit-btn">Submit</button>
  <button type="button" class="btn btn-default pull-right ld-cancel-btn">cancel</button>
 </div>
 </div>
 <div class="ld-panel">
 </div>


	</form>
</div>

  <script>
	$(document).ready(function() {
		
		FacilioApp.countryCombo('#select-country');
		FacilioApp.countryCombo('#country');
	});
  </script>
