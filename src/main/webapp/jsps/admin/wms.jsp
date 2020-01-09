<%@page import="com.facilio.wms.endpoints.LiveSession.LiveSessionType"%>
<%@page import="com.facilio.wms.endpoints.LiveSession"%>
<%@page import="java.util.Collection, java.util.Date"%>
<%@page import="com.facilio.wms.endpoints.SessionManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%

  Collection<LiveSession> appSessions = SessionManager.getInstance().getLiveSessions(LiveSessionType.APP);
  
  Collection<LiveSession> deviceSessions = SessionManager.getInstance().getLiveSessions(LiveSessionType.DEVICE);
  
  Collection<LiveSession> tvSessions = SessionManager.getInstance().getLiveSessions(LiveSessionType.REMOTE_SCREEN);
  %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
 <h4> Live App Users :  <%= (appSessions != null) ? appSessions.size() : 0 %> </h4>
<%if(appSessions!=null){ %>
<table style=" margin-top:40px;"  class="table table-bordered">
	<tr>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Session ID </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> User ID </th>
	<th class="org-th" style="max-width: 350px; width:350px;text-align: center;">Source</th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Connected Time  </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Last Msg Time</th>
	</tr>
	 <% 
	 for (LiveSession ses : appSessions) {
	 %>
	<tr id="id">
	<td  style="max-width: 350px;width:350px;" align="center"><%= ses.getKey() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=ses.getId() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%=ses.getLiveSessionSource() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getCreatedTime() > 0 ? new Date(ses.getCreatedTime()) : "--" %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getLastMsgTime() > 0 ? new Date(ses.getLastMsgTime()) : "--" %></td>
	</tr>
<%
}
%>
</table>
<%
}
%>

<br>
<h4> Live Devices :  <%= (deviceSessions != null) ? deviceSessions.size() : 0 %> </h4>
<%if(deviceSessions!=null){ %>
<table style=" margin-top:40px;"  class="table table-bordered">
	<tr>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Session ID </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Device ID </th>
	<th class="org-th" style="max-width: 350px; width:350px;text-align: center;">Source</th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Connected Time  </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Last Msg Time</th>
	</tr>
	 <% 
	 for (LiveSession ses : deviceSessions) {
	 %>
	<tr id="id">
	<td  style="max-width: 350px;width:350px;" align="center"><%= ses.getKey() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=ses.getId() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%=ses.getLiveSessionSource() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getCreatedTime() > 0 ? new Date(ses.getCreatedTime()) : "--" %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getLastMsgTime() > 0 ? new Date(ses.getLastMsgTime()) : "--" %></td>
	</tr>
<%
}
%>
</table>
<%
}
%>

<br>
<h4> Live TV Screens :  <%= (tvSessions != null) ? tvSessions.size() : 0 %> </h4>
<%if(tvSessions!=null){ %>
<table style=" margin-top:40px;"  class="table table-bordered">
	<tr>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Session ID </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Device ID </th>
	<th class="org-th" style="max-width: 350px; width:350px;text-align: center;">Source</th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Connected Time  </th>
	<th class="org-th" style="text-align:center;max-width: 350px;width:350px;"> Last Msg Time</th>
	</tr>
	 <% 
	 for (LiveSession ses : tvSessions) {
	 %>
	<tr id="id">
	<td  style="max-width: 350px;width:350px;" align="center"><%= ses.getKey() %></td>
	<td  style="max-width: 350px;width:350px;" align="center"><%=ses.getId() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%=ses.getLiveSessionSource() %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getCreatedTime() > 0 ? new Date(ses.getCreatedTime()) : "--" %></td>
	<td  style="max-width: 350px;width:350px;"  align="center"><%= ses.getLastMsgTime() > 0 ? new Date(ses.getLastMsgTime()) : "--" %></td>
	</tr>
<%
}
%>
</table>
<%
}
%>
</body>

</html>