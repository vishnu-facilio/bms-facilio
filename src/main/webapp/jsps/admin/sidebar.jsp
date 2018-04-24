<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="facilio-tags" prefix="f" %>
<div >
<div class="navbar-default sidebar " role="navigation">
    <div class="sidebar-nav navbar-collapse">
        <ul class="nav metismenu" id="side-menu">
        <!-- 
            <li class="sidebar-search">
                <div class="input-group custom-search-form">
                    <input type="text" class="form-control" placeholder="Search...">
                    <span class="input-group-btn">
                    <button class="btn btn-default" type="button" style="padding: 9px 12px;">
                        <i class="fa fa-search"></i>
                    </button>
                </span>
                </div>
               
            </li>
            -->

             <li>
               <a href="#dashboard">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-user fa-fw "></i> </span>
               <span class="nav-title">User Management</span>
               </a>
           </li>
                       <li >
               <a href="#">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-info-circle fa-fw "></i> </span>
               <span class="nav-title ">Build Info</span>
               </a>
           </li>
           <li>
               <a href="#dashboard">
               <span class="nav-icon">
               <i class=" sidebar-icon fa fa-terminal  fa-fw "></i> </span>
               <span class="nav-title">SQL Console</span>
               </a>
           </li>
           
         </ul>
        
      <div class="nav  sidebar-footer-btn  ">
        <div class="col-lg-12 col-centered" style="margin-bottom:30px">
		<a href="#" data-toggle="tooltip" data-placement="bottom" title="Build Version" > April 22 2018 (V17)</a>
			
        </div>
        </div>
    </div>
    
    
     
    </div>
    
        
        
        <script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();   
});
</script>
        
  

</div>