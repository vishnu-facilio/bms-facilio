<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
      <input type="text" name="name" value="helpdesk@facilio1234.freshservice.com" class="forwardMail" style="color:#0078bd;font-size:18px;font-weight:400;" disabled>
      <p class="subText2"><a href="#" data-toggle="tooltip" title="Learn how to configure your support email">How to convert your emails into Freshservice tickets?</a></p>       
    </div>
  	</div>

</div>