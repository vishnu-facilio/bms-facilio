<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="index"><img src="${pageContext.request.contextPath}/images/Brand-logo.svg">FACILIO<span style="color: #00BBE8 ">.</span></a>
    </div>
    <!-- /.navbar-header -->



	<div class="form-group">
    <ul class="nav navbar-top-links navbar-right">
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                <i class="fa fa-envelope icon-head-2x"></i>
            </a>

        </li>
        <li class="dropdown">
            <a class="dropdown-toggle" href="#help">
               <i class="fa fa-question-circle icon-head-2x" aria-hidden="true"></i>
            </a>
         
        </li>
        <li class="dropdown">
            <a class="dropdown-toggle" href="#setup">
               <i class="fa fa-gear icon-head-2x"></i>
            </a>
        </li>
        
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                         <img class="img-circle" width="30" height="30" alt="" src="https://www.shareicon.net/download/2016/09/15/829473_man.svg">
          <span class="arrow-center"></span>

            </a>
            <ul class="dropdown-menu dropdown-user">
                <li><a href="#"><i class="fa fa-user fa-fw"></i> User Profile</a>
                </li>
                <li><a href="#"><i class="fa fa-gear fa-fw"></i> Settings</a>
                </li>
                <li class="divider"></li>
                <li><a href="${pageContext.request.contextPath}/logout"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
                </li>
            </ul>
        </li>
    </ul>
 </div>
    <!-- /.navbar-top-links -->
</nav>
<%@ include file="sidebar.jsp" %>
    
<!-- /.navbar-static-side -->