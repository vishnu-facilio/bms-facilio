<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header"><a href="#users">Users</a>&nbsp;&nbsp;/&nbsp;&nbsp;<s:property value="user.name" /></h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-md-4 col-xs-12">
		<div class="white-box">
			<div class="user-bg"> 
				<div class="overlay-box">
					<div class="user-content">
						<a href="javascript:void(0)"><img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" class="thumb-lg img-circle" alt="img"></a>
						<h4><s:property value="user.name" /></h4>
						<h5><s:property value="user.email" /></h5>
						<h6><s:property value="user.getRoleAsString()" /></h6>
						<h6><i class="fa fa-clock-o" data-toggle="tooltip" data-placement="left" title="Last login" aria-hidden="true"></i> Jun 22, 2017</h6>
					</div>
				</div>
			</div>
			<div class="user-btm-box">
				<div class="col-md-4 col-sm-4 text-center">
					<button type="button" class="btn btn-outline btn-primary btn-md" data-toggle="tooltip" data-placement="bottom" title="Reset password">
						<i class="fa fa-key fa-2x"></i>
					</button>
				</div>
				<div class="col-md-4 col-sm-4 text-center">
					<button type="button" class="btn btn-outline btn-primary btn-md" data-toggle="tooltip" data-placement="bottom" title="Edit user">
						<i class="fa fa-pencil fa-2x"></i>
					</button>
				</div>
				<div class="col-md-4 col-sm-4 text-center">
					<button type="button" class="btn btn-outline btn-danger btn-md" data-toggle="tooltip" data-placement="bottom" title="Delete user">
						<i class="fa fa-trash fa-2x"></i>
					</button>
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-8 col-xs-12">
		<div class="white-box">
			<ul class="nav nav-tabs tabs customtab">
				<li class="tab active">
					<a href="#home" data-toggle="tab" aria-expanded="true"> <span class="visible-xs"><i class="fa fa-home"></i></span> <span class="hidden-xs">Activity</span></a>
				</li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="home">
					<div class="steamline">
						<div class="sl-item">
							<div class="sl-left"> <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="user" class="img-circle"> </div>
							<div class="sl-right">
								<div class="m-l-40"><a href="#" class="text-info">John Doe</a> <span class="sl-date">5 minutes ago</span><p class="m-t-10" style="
"> Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper </p></div>
							</div>
						</div>
						<div class="sl-item">
							<div class="sl-left"> <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="user" class="img-circle"> </div>
							<div class="sl-right">
								<div class="m-l-40"><a href="#" class="text-info">John Doe</a> <span class="sl-date">5 minutes ago</span><p>assign a new task <a href="#"> Design weblayout</a></p></div>
							</div>
						</div>
						<div class="sl-item">
							<div class="sl-left"> <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="user" class="img-circle"> </div>
							<div class="sl-right">
								<div class="m-l-40"><a href="#" class="text-info">John Doe</a> <span class="sl-date">5 minutes ago</span><p>assign a new task <a href="#"> Design weblayout</a></p></div>
							</div>
						</div>
						<div class="sl-item">
							<div class="sl-left"><img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="user" class="img-circle"> </div>
							<div class="sl-right">
								<div class="m-l-40"><a href="#" class="text-info">John Doe</a> <span class="sl-date">5 minutes ago</span><p>assign a new task <a href="#"> Design weblayout</a></p></div>
							</div>
						</div>
						<div class="sl-item">
							<div class="sl-left"> <img src="https://www.shareicon.net/download/2016/09/15/829473_man.svg" alt="user" class="img-circle"> </div>
							<div class="sl-right">
								<div class="m-l-40"><a href="#" class="text-info">John Doe</a> <span class="sl-date">5 minutes ago</span><p>assign a new task <a href="#"> Design weblayout</a></p></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.white-box {
    background: #f8f8f8;
    padding: 25px;
    margin-bottom: 30px;
}

.user-bg {
    margin: -25px;
}
.user-bg .overlay-box {
    background: #f8f8f8;
    opacity: 0.9;
    text-align: center;
}
.user-bg .overlay-box .user-content {
    padding: 15px;
    margin-top: 30px;
}
.thumb-lg {
    height: 88px;
    width: 88px;
}
scaffolding.less:115
.img-circle {
    border-radius: 50%;
}
.text-white {
    color: #ffffff;
}

h4, h5, h6 {
    line-height: 18px;
    font-weight:400;
}
.user-btm-box {
    padding: 40px 0 10px;
    clear: both;
    overflow: hidden;
    font-size: 13px;
}

.steamline {
    position: relative;
    border-left: 1px solid rgba(120, 130, 140, 0.13);
    margin-left: 20px;
}
.steamline .sl-item {
    border-bottom: 1px solid rgba(120, 130, 140, 0.13);
    margin: 20px 0;
}
.steamline .sl-left {
    float: left;
    margin-left: -20px;
    z-index: 1;
    width: 40px;
    line-height: 40px;
    text-align: center;
    height: 40px;
    border-radius: 100%;
    color: #ffffff;
    background: #313131;
    margin-right: 15px;
}
.steamline .sl-left img {
    max-width: 40px;
}
.steamline .sl-right {
    padding-left: 50px;
}
.m-l-40 {
    margin-left: 40px !important;
}
.steamline .sl-right div > a {
    color: #313131;
    font-weight: 400;
}
.sl-date {
    font-size: 10px;
    color: #98a6ad;
}

p {
    line-height: 1.6;
    font-size: 12px;
}

.customtab {
    border-bottom: 2px solid #f7fafc;
}

.customtab li.active a, .customtab li.active a:hover, .customtab li.active a:focus {
    border-bottom: 2px solid #2cabe3;
    color: #2cabe3;
}

.nav-tabs {
    border-bottom: 1px solid #ddd;
}
.nav-tabs>li.active>a, .nav-tabs>li.active>a:focus, .nav-tabs>li.active>a:hover {
    background: none !important;
    border: none !important;
}
</style>
<script>
$('.white-box').tooltip({
    selector: "[data-toggle=tooltip]",
    container: "body"
});
</script>