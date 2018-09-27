<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    %>
 <%@page import="com.facilio.accounts.util.AccountUtil,com.facilio.bmsconsole.context.EnergyMeterContext, com.facilio.accounts.impl.OrgBeanImpl, com.facilio.accounts.bean.OrgBean, com.facilio.fw.BeanFactory, com.facilio.bmsconsole.actions.EnergyAction, com.facilio.accounts.dto.User, java.util.*, java.util.Iterator ,org.json.simple.JSONObject,org.json.simple.JSONArray,java.util.List, com.facilio.accounts.dto.Organization ,org.json.simple.JSONObject,com.facilio.accounts.impl.OrgBeanImpl, com.facilio.bmsconsole.commands.util.CommonCommandUtil"%>
    
    
      <%
  String orgid = request.getParameter("orgid");
    Organization org = null;
    List<EnergyMeterContext> meters = new ArrayList<>();
    List<String> meterName = new ArrayList<>();
    if (orgid != null) {
  	  org = AccountUtil.getOrgBean().getOrg(Long.parseLong(orgid));
  	  OrgBean orgBean = (OrgBean) BeanFactory.lookup("OrgBean", Long.parseLong(orgid));
  	  meters = (List<EnergyMeterContext>)orgBean.getEnergyMeterList();
  		for (EnergyMeterContext meter : meters) {
  			 meterName.add(meter.getName());
		}  	  
  	}
  %>
  <%
     String Value1 = request.getParameter("meterName");
     String value2 = request.getParameter("date");
     String value3 = request.getParameter("Hour");
   %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
 <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
</head>
<body>

 <form action="" method="GET">
 <h2><i class=" fa fa-user fa-fw "></i>Anomaly Console</h2>
 <div class=" col-lg-8 col-md-8">
    <div style="margin-top:40px;" class="input-group col-lg-8 col-md-8 col-sm-8	">
      <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
      <input id="anomalyconsole" type="text" value="<%= org == null ? "" : org.getId() %>" class="form-control" name = "orgid"  placeholder="orgId" />
    </div>
    <div style="margin-top:30px;">
<button  id="show" type="submit"  >Submit</button>
</form>

 <form action="" method="POST">
<% if (org != null) {  %>
<div style="margin-top:30px">
Energy Meter: <select name = "meterName">
<% for (int i =0 ; i<meterName.size() ; i++) {  %>
		<option value="<%= meterName.get(i) %>"> <%= meterName.get(i) %> </option>
		<% } %>
</select>
Date: <input type="text" id="datepicker" value="date" name = "date"  placeholder="date" />
Hour: <select name = "Hour"> 
<% for (int i =0 ; i<=23 ; i++) {  %>
		<option value="<%= i %>" ><%= i %></option>
<% } %>
</select>
 <input type = "submit" value = "Submit" />
<!-- <button type="submit" >Apply</button> --> 
<%
 String date = request.getParameter("date");
if (date != null) { %>
 <div id="myDiv"></div>
 <% } %>
</div>
<% 
%>
<% } %>
</div>
</div>
</form>
</body>
<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>jQuery UI Datepicker - Default functionality</title>
  <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  <link rel="stylesheet" href="/resources/demos/style.css">
  <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
  <script>
  $( function() {
    $( "#datepicker" ).datepicker();
  } );
  </script>
 <script >
 function normal() {
    var x = 0,
        y = 0,
        rds, c;
    do {
        x = Math.random() * 2 - 1;
        y = Math.random() * 2 - 1;
        rds = x * x + y * y;
    } while (rds == 0 || rds > 1);
    c = Math.sqrt(-2 * Math.log(rds) / rds); 
    return x * c; // throw away extra sample y * c
}

var N = 2000,
  a = -1,
  b = 1.2;

var step = (b - a) / (N - 1);
var t = new Array(N), x = new Array(N), y = new Array(N);

for(var i = 0; i < N; i++){
  t[i] = a + step * i;
  x[i] = (Math.pow(t[i], 3)) + (0.3 * normal() );
  y[i] = (Math.pow(t[i], 6)) + (0.3 * normal() );
}

var trace1 = {
  x: x,
  y: y,
  mode: 'markers',
  name: 'points',
  marker: {
    color: 'rgb(102,0,0)',
    size: 2,
    opacity: 0.4
  },
  type: 'scatter'
};
var trace2 = {
  x: x,
  y: y,
  name: 'density',
  ncontours: 20,
  colorscale: 'Hot',
  reversescale: true,
  showscale: false,
  type: 'histogram2dcontour'
};
var trace3 = {
  x: x,
  name: 'x density',
  marker: {color: 'rgb(102,0,0)'},
  yaxis: 'y2',
  type: 'histogram'
};
var trace4 = {
  y: y,
  name: 'y density',
  marker: {color: 'rgb(102,0,0)'},
  xaxis: 'x2',
  type: 'histogram'
};
var data = [trace1, trace2, trace3, trace4];
var layout = {
  showlegend: false,
  autosize: false,
  width: 600,
  height: 550,
  margin: {t: 50},
  hovermode: 'closest',
  bargap: 0,
  xaxis: {
    domain: [0, 0.85],
    showgrid: false,
    zeroline: false
  },
  yaxis: {
    domain: [0, 0.85],
    showgrid: false,
    zeroline: false
  },
  xaxis2: {
    domain: [0.85, 1],
    showgrid: false,
    zeroline: false
  },
  yaxis2: {
    domain: [0.85, 1],
    showgrid: false,
    zeroline: false
  }
};
Plotly.newPlot('myDiv', data, layout);
</script> 
</html>