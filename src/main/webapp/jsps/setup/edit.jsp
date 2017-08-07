<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<div class="container1 adminContent">
  <div class="row">
    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left"> <i class="fa fa-arrow-left" aria-hidden="true"></i>
      <a href="#" class="emailBack">Back to Email Configs</a>
    </div>
    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right"> <a href="#" class="cancel">Cancel</a> <a href="#" class="update">Update</a> </div>
  </div>
</div>
<div class="container-fluid1">
  <div class="row">
    <div class="editBorder"></div>
  </div>
</div>
<div class="container1">
  <div class="row">
    <div class="heading1">
      <h3>Email Settings</h3>
    </div>
  </div>
</div>

<div class="container1">
  <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Name</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="text" name="name" value="facilio" class="txtName">
      <p class="subText1">Name of the email that will be used in the ticket replies</p>       
    </div>
  </div>
   <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Your helpdesk email <span class="star"> *</span></p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="text" name="name" value="helpdesk@facilio1234.freshservice.com" class="txtName">
      <p class="subText1">This is also your Reply-to address eg. helpdesk@yourcompany.com</p>       
    </div>
  </div>
 <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
      <p class="name">Assign to Group</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      
      <select>
  <option value="0">...</option>
  <option value="1">Capacity Management Team</option>
  <option value="2">Change Team</option>
  <option value="3">Database Team</option>
  <option value="4">Hardware Team</option>
  <option value="5">Incident Team</option>
  <option value="6">Major Incident Team</option>
  <option value="7">Problem Management Team</option>
  <option value="8">Release Team</option>
  <option value="9">Service Design Team</option>
  <option value="10">Service Request Fulfillment Team</option>
  <option value="11">Supplier Management Team</option>
</select>
      <p class="subText1">New tickets to this support email will get automatically assigned to a group</p>       
    </div>
  </div>
   <div class="row">
        <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
          <p class="name">Mail Server:</p>    
        </div>
        <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
            <form>
                <input type="radio"  name="default" checked> Default (Freshservice)
                <input type="radio" name="default" > Use your own mail server
            </form>    
        </div>
  	</div>
     <div class="row">
        <div class="col-xs-4 col-sm-4 col-md-4 col-lg-3">
          <p class="name">Forward your emails to:</p>    
        </div>
        <div class="col-xs-8 col-sm-8 col-md-8 col-lg-9">
      <input type="text" name="name" value="helpdesk@facilio1234.freshservice.com" class="forwardMail" disabled>
      <p class="subText1">How to convert your emails into Freshservice tickets?</p>       
    </div>
  	</div>

</div>