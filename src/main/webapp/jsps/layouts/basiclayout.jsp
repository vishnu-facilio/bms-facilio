<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<title><tiles:getAsString name="title" /></title>  
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css">
<!-- <link rel="stylesheet" href="http://www.jqueryscript.net/demo/Stylish-Multi-level-Sidebar-Menu-Plugin-With-jQuery-sidebar-menu-js/dist/sidebar-menu.css"> -->

<link rel="stylesheet" href="../css/sidebar-menu.css">

</head>  
<body>  
  
  <table style="height: 90px;" width="1200px" border="1">
<tbody>

<tr>
<td rowspan="2" style="width: 200px">

<section style="width: 200px">
    <ul class="sidebar-menu">
      <li class="header">MAIN NAVIGATION</li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-dashboard"></i> <span>Dashboard</span> <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> Dashboard v1</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Dashboard v2</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-files-o"></i>
        <span>Layout Options</span>
          <span class="label label-primary pull-right">4</span>
        </a>
        <ul class="treeview-menu" style="display: none;">
          <li><a href="#"><i class="fa fa-circle-o"></i> Top Navigation</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Boxed</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Fixed</a></li>
          <li class=""><a href="#"><i class="fa fa-circle-o"></i> Collapsed Sidebar</a>
          </li>
        </ul>
      </li>
      <li>
        <a href="#">
          <i class="fa fa-th"></i> <span>Widgets</span>
          <small class="label pull-right label-info">new</small>
        </a>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-pie-chart"></i>
        <span>Charts</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> ChartJS</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Morris</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Flot</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Inline charts</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-laptop"></i>
        <span>UI Elements</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> General</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Icons</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Buttons</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Sliders</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Timeline</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Modals</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-edit"></i> <span>Forms</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> General Elements</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Advanced Elements</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Editors</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-table"></i> <span>Tables</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> Simple tables</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Data tables</a></li>
        </ul>
      </li>
      <li>
        <a href="#">
          <i class="fa fa-calendar"></i> <span>Calendar</span>
          <small class="label pull-right label-danger">3</small>
        </a>
      </li>
      <li>
        <a href="#">
          <i class="fa fa-envelope"></i> <span>Mailbox</span>
          <small class="label pull-right label-warning">12</small>
        </a>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-folder"></i> <span>Examples</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> Invoice</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Profile</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Login</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Register</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Lockscreen</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> 404 Error</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> 500 Error</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Blank Page</a></li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Pace Page</a></li>
        </ul>
      </li>
      <li class="treeview">
        <a href="#">
          <i class="fa fa-share"></i> <span>Multilevel</span>
          <i class="fa fa-angle-left pull-right"></i>
        </a>
        <ul class="treeview-menu">
          <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
          <li>
            <a href="#"><i class="fa fa-circle-o"></i> Level One <i class="fa fa-angle-left pull-right"></i></a>
            <ul class="treeview-menu">
              <li><a href="#"><i class="fa fa-circle-o"></i> Level Two</a></li>
              <li>
                <a href="#"><i class="fa fa-circle-o"></i> Level Two <i class="fa fa-angle-left pull-right"></i></a>
                <ul class="treeview-menu">
                  <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                  <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                </ul>
              </li>
            </ul>
          </li>
          <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
        </ul>
      </li>
      <li><a href="#"><i class="fa fa-book"></i> <span>Documentation</span></a></li>
      <li class="header">LABELS</li>
      <li><a href="#"><i class="fa fa-circle-o text-red"></i> <span>Important</span></a></li>
      <li><a href="#"><i class="fa fa-circle-o text-yellow"></i> <span>Warning</span></a></li>
      <li><a href="#"><i class="fa fa-circle-o text-aqua"></i> <span>Information</span></a></li>
    </ul>
  </section>

</td>
<td align="top" style="height:50px">

<%@  include file="header.jsp" %>  
</td>

</tr>
<tr><td>

<tiles:insertAttribute name="body" />  
</td>
</tr>
<tr>
<td colspan="2"><%@ include file="footer.jsp" %>  
</td>
</tr>
</tbody>
</table>
  
<script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
 <script src="http://www.jqueryscript.net/demo/Stylish-Multi-level-Sidebar-Menu-Plugin-With-jQuery-sidebar-menu-js/dist/sidebar-menu.js"></script> 
 
<script>
$.sidebarMenu($('.sidebar-menu'))
</script>
  
</body> 
</html>   
