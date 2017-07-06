<%@ taglib uri="facilio-tags" prefix="f" %> 

<script>

var chart1;
var chart2;
var chart3;
function showDeviceData(controllerId)
{
	var dataObject = new Object();
	//dataObject.controllerId = controllerId;
	$.ajax({
	      type: "GET",
	      url: contextPath + "/home/showDeviceData",   
	      data: dataObject,
	      success: function (response) {
	    	console.log(response);
    		var chart_json = 
			{
				'data': 
					{
						'x': 'x',
						'columns': 
							[
								response.x,
								response.y
							]
					},
				'axis': 
					{
						'x': 
							{
								'label': 'Time',
								'type': 'timeseries',
								'tick': {'format': '%H:%M:%S'}
							},
						'y': 
							{
								'label': 'kW'
							}
					}
			};
			chart1 = ChartLibrary.timeseries("#device-energy-usage", chart_json);
			var chart_json = {
			    data: {
			        columns: [
			            ['data1', 30, 200, 100, 400, 150, 250],
			        ],
			        type: 'bar'
			    },
			    bar: {
			        width: {
			            ratio: 0.2
			        }
			    }
			};
			chart2 = ChartLibrary.barchart("#device-frequency-usage", chart_json);
			
			var chart_json2 = {
			    data: {
			        columns: [
			            ['data1', 30],
			            ['data2', 120],
			            ['data3', 10]
			        ],
			        type : 'donut',
			        onclick: function (d, i) { console.log("onclick", d, i); },
			        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
			        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
			    },
			    donut: {
			        title: "Iris Petal Width"
			    }
			};
			chart3 = ChartLibrary.donutchart("#device-voltage-usage", chart_json2);
	      }
	 });
}

$( document ).ready(function() {
	showDeviceData(1);
});

</script>

<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">Dashboard</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<!-- /.row -->
<div class="row">
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-red">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-support fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">13</div>
                        <div>New Requests!</div>
                    </div>
                </div>
            </div>
            <a href="#">
                <div class="panel-footer">
                    <span class="pull-left">View Details</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-green">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-tasks fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">12</div>
                        <div>New Tasks!</div>
                    </div>
                </div>
            </div>
            <a href="#">
                <div class="panel-footer">
                    <span class="pull-left">View Details</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-yellow">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-laptop fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">124</div>
                        <div>New Devices!</div>
                    </div>
                </div>
            </div>
            <a href="#">
                <div class="panel-footer">
                    <span class="pull-left">View Details</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
    <div class="col-lg-3 col-md-6">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-xs-3">
                        <i class="fa fa-user fa-5x"></i>
                    </div>
                    <div class="col-xs-9 text-right">
                        <div class="huge">26</div>
                        <div>Users!</div>
                    </div>
                </div>
            </div>
            <a href="#users">
                <div class="panel-footer">
                    <span class="pull-left">View Details</span>
                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                    <div class="clearfix"></div>
                </div>
            </a>
        </div>
    </div>
</div>
<!-- /.row -->
<div class="row">
    <div class="col-lg-8">
        <div class="panel panel-default">
            <div class="panel-heading">
                <i class="fa fa-bar-chart-o fa-fw"></i> Total Energy Consumption
                <div class="pull-right">
                    <div class="btn-group">
                        <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                            Actions
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu pull-right" role="menu">
                            <li><a href="#">Action</a>
                            </li>
                            <li><a href="#">Another action</a>
                            </li>
                            <li><a href="#">Something else here</a>
                            </li>
                            <li class="divider"></li>
                            <li><a href="#">Separated link</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <!-- /.panel-heading -->
           <div class="panel-body">
               <div id="device-energy-usage">
               </div>
           </div>
           <!-- /.panel-body -->
       </div>
       <!-- /.panel -->
       <div class="panel panel-default">
           <div class="panel-heading">
               <i class="fa fa-bar-chart-o fa-fw"></i> Bar Chart
               <div class="pull-right">
                   <div class="btn-group">
                       <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                           Actions
                           <span class="caret"></span>
                       </button>
                       <ul class="dropdown-menu pull-right" role="menu">
                           <li><a href="#">Action</a>
                           </li>
                           <li><a href="#">Another action</a>
                           </li>
                           <li><a href="#">Something else here</a>
                           </li>
                           <li class="divider"></li>
                           <li><a href="#">Separated link</a>
                           </li>
                       </ul>
                   </div>
               </div>
           </div>
           <!-- /.panel-heading -->
           <div class="panel-body">
               <div id="device-frequency-usage">
               </div>
           </div>
           <!-- /.panel-body -->
       </div>
       <!-- /.panel -->
       <%--<div class="panel panel-default">
           <div class="panel-heading">
               <i class="fa fa-clock-o fa-fw"></i> Responsive Timeline
           </div>
           <!-- /.panel-heading -->
           <div class="panel-body">
               <ul class="timeline">
                   <li>
                       <div class="timeline-badge"><i class="fa fa-check"></i>
                       </div>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                               <p><small class="text-muted"><i class="fa fa-clock-o"></i> 11 hours ago via Twitter</small>
                               </p>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Libero laboriosam dolor perspiciatis omnis exercitationem. Beatae, officia pariatur? Est cum veniam excepturi. Maiores praesentium, porro voluptas suscipit facere rem dicta, debitis.</p>
                           </div>
                       </div>
                   </li>
                   <li class="timeline-inverted">
                       <div class="timeline-badge warning"><i class="fa fa-credit-card"></i>
                       </div>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Autem dolorem quibusdam, tenetur commodi provident cumque magni voluptatem libero, quis rerum. Fugiat esse debitis optio, tempore. Animi officiis alias, officia repellendus.</p>
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Laudantium maiores odit qui est tempora eos, nostrum provident explicabo dignissimos debitis vel! Adipisci eius voluptates, ad aut recusandae minus eaque facere.</p>
                           </div>
                       </div>
                   </li>
                   <li>
                       <div class="timeline-badge danger"><i class="fa fa-bomb"></i>
                       </div>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Repellendus numquam facilis enim eaque, tenetur nam id qui vel velit similique nihil iure molestias aliquam, voluptatem totam quaerat, magni commodi quisquam.</p>
                           </div>
                       </div>
                   </li>
                   <li class="timeline-inverted">
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptates est quaerat asperiores sapiente, eligendi, nihil. Itaque quos, alias sapiente rerum quas odit! Aperiam officiis quidem delectus libero, omnis ut debitis!</p>
                           </div>
                       </div>
                   </li>
                   <li>
                       <div class="timeline-badge info"><i class="fa fa-save"></i>
                       </div>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Nobis minus modi quam ipsum alias at est molestiae excepturi delectus nesciunt, quibusdam debitis amet, beatae consequuntur impedit nulla qui! Laborum, atque.</p>
                               <hr>
                               <div class="btn-group">
                                   <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
                                       <i class="fa fa-gear"></i> <span class="caret"></span>
                                   </button>
                                   <ul class="dropdown-menu" role="menu">
                                       <li><a href="#">Action</a>
                                       </li>
                                       <li><a href="#">Another action</a>
                                       </li>
                                       <li><a href="#">Something else here</a>
                                       </li>
                                       <li class="divider"></li>
                                       <li><a href="#">Separated link</a>
                                       </li>
                                   </ul>
                               </div>
                           </div>
                       </div>
                   </li>
                   <li>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Sequi fuga odio quibusdam. Iure expedita, incidunt unde quis nam! Quod, quisquam. Officia quam qui adipisci quas consequuntur nostrum sequi. Consequuntur, commodi.</p>
                           </div>
                       </div>
                   </li>
                   <li class="timeline-inverted">
                       <div class="timeline-badge success"><i class="fa fa-graduation-cap"></i>
                       </div>
                       <div class="timeline-panel">
                           <div class="timeline-heading">
                               <h4 class="timeline-title">Lorem ipsum dolor</h4>
                           </div>
                           <div class="timeline-body">
                               <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Deserunt obcaecati, quaerat tempore officia voluptas debitis consectetur culpa amet, accusamus dolorum fugiat, animi dicta aperiam, enim incidunt quisquam maxime neque eaque.</p>
                           </div>
                       </div>
                   </li>
               </ul>
           </div>
           <!-- /.panel-body -->
       </div>
       <!-- /.panel -->
        --%>
   </div>
   <!-- /.col-lg-8 -->
   <div class="col-lg-4">
       <div class="panel panel-default">
           <div class="panel-heading">
               <i class="fa fa-bell fa-fw"></i> Notifications Panel
           </div>
           <!-- /.panel-heading -->
           <div class="panel-body">
               <div class="list-group">
                   <a href="#" class="list-group-item">
                       <i class="fa fa-comment fa-fw"></i> New Comment
                       <span class="pull-right text-muted small"><em>4 minutes ago</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-twitter fa-fw"></i> 3 New Followers
                       <span class="pull-right text-muted small"><em>12 minutes ago</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-envelope fa-fw"></i> Message Sent
                       <span class="pull-right text-muted small"><em>27 minutes ago</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-tasks fa-fw"></i> New Task
                       <span class="pull-right text-muted small"><em>43 minutes ago</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-upload fa-fw"></i> Server Rebooted
                       <span class="pull-right text-muted small"><em>11:32 AM</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-bolt fa-fw"></i> Server Crashed!
                       <span class="pull-right text-muted small"><em>11:13 AM</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-warning fa-fw"></i> Server Not Responding
                       <span class="pull-right text-muted small"><em>10:57 AM</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-shopping-cart fa-fw"></i> New Order Placed
                       <span class="pull-right text-muted small"><em>9:49 AM</em>
                       </span>
                   </a>
                   <a href="#" class="list-group-item">
                       <i class="fa fa-money fa-fw"></i> Payment Received
                       <span class="pull-right text-muted small"><em>Yesterday</em>
                       </span>
                   </a>
               </div>
               <!-- /.list-group -->
               <a href="#" class="btn btn-default btn-block">View All Alerts</a>
           </div>
           <!-- /.panel-body -->
       </div>
       <!-- /.panel -->
       <div class="panel panel-default">
           <div class="panel-heading">
               <i class="fa fa-bar-chart-o fa-fw"></i> Donut Chart
               <div class="pull-right">
                   <div class="btn-group">
                       <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                           Actions
                           <span class="caret"></span>
                       </button>
                       <ul class="dropdown-menu pull-right" role="menu">
                           <li><a href="#">Action</a>
                           </li>
                           <li><a href="#">Another action</a>
                           </li>
                           <li><a href="#">Something else here</a>
                           </li>
                           <li class="divider"></li>
                           <li><a href="#">Separated link</a>
                           </li>
                       </ul>
                   </div>
               </div>
           </div>
           <!-- /.panel-heading -->
           <div class="panel-body">
               <div id="device-voltage-usage">
               </div>
           </div>
           <!-- /.panel-body -->
       </div>
       <!-- /.panel -->
       <%--<div class="chat-panel panel panel-default">
           <div class="panel-heading">
               <i class="fa fa-comments fa-fw"></i> Chat
               <div class="btn-group pull-right">
                   <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown">
                       <i class="fa fa-chevron-down"></i>
                   </button>
                   <ul class="dropdown-menu slidedown">
                       <li>
                           <a href="#">
                               <i class="fa fa-refresh fa-fw"></i> Refresh
                           </a>
                       </li>
                       <li>
                           <a href="#">
                               <i class="fa fa-check-circle fa-fw"></i> Available
                           </a>
                       </li>
                       <li>
                           <a href="#">
                               <i class="fa fa-times fa-fw"></i> Busy
                           </a>
                       </li>
                       <li>
                           <a href="#">
                               <i class="fa fa-clock-o fa-fw"></i> Away
                           </a>
                       </li>
                       <li class="divider"></li>
                       <li>
                           <a href="#">
                               <i class="fa fa-sign-out fa-fw"></i> Sign Out
                           </a>
                       </li>
                   </ul>
               </div>
           </div>
           <!-- /.panel-heading -->
           <div class="panel-body">
               <ul class="chat">
                   <li class="left clearfix">
                       <span class="chat-img pull-left">
                           <img src="http://placehold.it/50/55C1E7/fff" alt="User Avatar" class="img-circle" />
                       </span>
                       <div class="chat-body clearfix">
                           <div class="header">
                               <strong class="primary-font">Jack Sparrow</strong>
                               <small class="pull-right text-muted">
                                   <i class="fa fa-clock-o fa-fw"></i> 12 mins ago
                               </small>
                           </div>
                           <p>
                               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.
                           </p>
                       </div>
                   </li>
                   <li class="right clearfix">
                       <span class="chat-img pull-right">
                           <img src="http://placehold.it/50/FA6F57/fff" alt="User Avatar" class="img-circle" />
                       </span>
                       <div class="chat-body clearfix">
                           <div class="header">
                               <small class=" text-muted">
                                   <i class="fa fa-clock-o fa-fw"></i> 13 mins ago</small>
                               <strong class="pull-right primary-font">Bhaumik Patel</strong>
                           </div>
                           <p>
                               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.
                           </p>
                       </div>
                   </li>
                   <li class="left clearfix">
                       <span class="chat-img pull-left">
                           <img src="http://placehold.it/50/55C1E7/fff" alt="User Avatar" class="img-circle" />
                       </span>
                       <div class="chat-body clearfix">
                           <div class="header">
                               <strong class="primary-font">Jack Sparrow</strong>
                               <small class="pull-right text-muted">
                                   <i class="fa fa-clock-o fa-fw"></i> 14 mins ago</small>
                           </div>
                           <p>
                               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.
                           </p>
                       </div>
                   </li>
                   <li class="right clearfix">
                       <span class="chat-img pull-right">
                           <img src="http://placehold.it/50/FA6F57/fff" alt="User Avatar" class="img-circle" />
                       </span>
                       <div class="chat-body clearfix">
                           <div class="header">
                               <small class=" text-muted">
                                   <i class="fa fa-clock-o fa-fw"></i> 15 mins ago</small>
                               <strong class="pull-right primary-font">Bhaumik Patel</strong>
                           </div>
                           <p>
                               Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.
                           </p>
                       </div>
                   </li>
               </ul>
           </div>
           <!-- /.panel-body -->
           <div class="panel-footer">
               <div class="input-group">
                   <input id="btn-input" type="text" class="form-control input-sm" placeholder="Type your message here..." />
                   <span class="input-group-btn">
                       <button class="btn btn-warning btn-sm" id="btn-chat">
                           Send
                       </button>
                   </span>
               </div>
           </div>
           <!-- /.panel-footer -->
       </div>
       <!-- /.panel .chat-panel -->
        --%>
   </div>
   <!-- /.col-lg-4 -->
</div>
<!-- /.row -->