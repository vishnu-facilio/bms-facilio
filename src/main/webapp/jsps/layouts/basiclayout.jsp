<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  
 "http://www.w3.org/TR/html4/loose.dtd">  
<html>  
<head>  
<title><tiles:getAsString name="title" /></title>  
</head>  
<body>  
  
  <table style="height: 90px;" width="800px">
<tbody>
<tr>
<td colspan="2"><%@  include file="header.jsp" %>  
</td>
</tr>
<tr>
<td><img src="../images/leftmenu.png"></img></td>
<td><tiles:insertAttribute name="body" />  
</td>
</tr>
<tr>
<td colspan="2"><%@ include file="footer.jsp" %>  
</td>
</tr>
</tbody>
</table>
  
  
</body>  
</html>   
