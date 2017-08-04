<div class="container adminContent">
  <div class="row">
    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left"> <i class="fa fa-arrow-left" aria-hidden="true"></i>
      <a href="#" class="emailBack">Back to Email Configs</a>
    </div>
    <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right"> <a href="#" class="cancel">Cancel</a> <a href="#" class="update">Update</a> </div>
  </div>
</div>
<div class="container-fluid">
  <div class="row">
    <div class="border"></div>
  </div>
</div>
<div class="container">
  <div class="row">
    <div class="heading1">
      <h3>Email Settings</h3>
    </div>
  </div>
</div>

<div class="container">
  <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-2">
      <p class="name">Name</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-10">
      <input type="text" name="name" value="facilio" class="txtName">
      <p class="subText">Name of the email that will be used in the ticket replies</p>       
    </div>
  </div>
   <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-2">
      <p class="name">Your helpdesk email <span class="star"> *</span></p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-10">
      <input type="text" name="name" value="helpdesk@facilio1234.freshservice.com" class="txtName">
      <p class="subText">This is also your Reply-to address eg. helpdesk@yourcompany.com</p>       
    </div>
  </div>
 <div class="row">
    <div class="col-xs-4 col-sm-4 col-md-4 col-lg-2">
      <p class="name">Assign to Group</p>    
    </div>
    <div class="col-xs-8 col-sm-8 col-md-8 col-lg-10">
      
      <select>
  <option value="volvo">...</option>
  <option value="saab">Capacity Management Team</option>
  <option value="opel">Change Team</option>
  <option value="audi">Database Team</option>
  <option value="saab">Hardware Team</option>
  <option value="opel">Incident Team</option>
  <option value="audi">Major Incident Team</option>
  <option value="saab">Problem Management Team</option>
  <option value="opel">Release Team</option>
  <option value="audi">Service Design Team</option>
  <option value="saab">Service Request Fulfillment Team</option>
  <option value="opel">Supplier Management Team</option>
</select>
      <p class="subText">New tickets to this support email will get automatically assigned to a group</p>       
    </div>
  </div>
   <div class="row">
        <div class="col-xs-4 col-sm-4 col-md-4 col-lg-2">
          <p class="name">Mail Server:</p>    
        </div>
        <div class="col-xs-8 col-sm-8 col-md-8 col-lg-10">
            <form>
                <input type="radio"  name="default" checked> Default (Freshservice)
                <input type="radio" name="default" > Use your own mail server
            </form>    
        </div>
  	</div>
     <div class="row">
        <div class="col-xs-4 col-sm-4 col-md-4 col-lg-2">
          <p class="name">Forward your emails to:</p>    
        </div>
        <div class="col-xs-8 col-sm-8 col-md-8 col-lg-10">
      <input type="text" name="name" value="helpdesk@facilio1234.freshservice.com" class="forwardMail" disabled>
      <p class="subText">How to convert your emails into Freshservice tickets?</p>       
    </div>
  	</div>

</div>