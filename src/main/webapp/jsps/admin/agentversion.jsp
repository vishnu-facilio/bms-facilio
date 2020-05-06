<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Agent Version</title>
</head>
<script>
function myFunction() {
  var x = document.getElementById("new");
  if (x.style.display === "block") {
    x.style.display = "none";
  } else {
    x.style.display = "block";
  }
}
</script>
<script>
function validateForm() {
	  var ver = document.forms["myForm"]["version"].value;
	  var desc = document.forms["myForm"]["desc"].value;
	  var createdby =document.forms["myForm"]["user"].value;
	  var url = document.forms["myForm"]["url"].value;	  
	  if (ver == "" || ver == null) {
	    alert("version must be filled out");
	    return false;
	  }else if(desc == "" || desc == null){
		  alert("description must be filled out");
		  return false;
	  }else if(createdby == "" || createdby == null){
		  alert("createdBy must be filled out"); 
		  return false;
	  }else if(url == "" || url == null){
		  alert("url must be filled out");  
		  return false;
	  }
	}
</script>
<body>
<button onclick="myFunction()" class="button button1">ADD VERSION</button>
<div id="new" style="display:none">

<form name="myForm" action = "addAgentVersion"  onsubmit="return validateForm()" required>
</br></br>
	<div >
			<fieldset>
			<label for="txtClassroomName"><h5>Enter Version: </h5> </label><input type = "text" name = "version" size="5"/></br></br>
			<label for="txtClassroomName"><h5>Enter Description:</h5>  </label><input type = "text" name = "desc"   size="5"/></br></br>
			<label for="txtClassroomName"><h5>Enter CreatedBy:</h5> </label><input type = "text" name = "user"   size="5"/></br></br>
			<label for="txtClassroomName"><h5>Enter URL:</h5>    </label><input type = "text" name = "url"  size="5"/>
			</fieldset>
			</br></br>
			
			<input type = "submit"  style="margin-left: 200px" name="addAgentVersion"  value = "Submit"/> 
			
			<br>
			<br>
			<br>
		</div> 

</form>
</div>
</body>
<style>
input[type=text] {
	width: 20%;
	padding: 12px 20px;
	margin: 8px 16px;
	margin-top: 8px;
	display: inline-block;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}
input[type=submit] {
	width: 10%;
	background-color: #4CAF50;
	color: white;
	padding: 11px 9px;
	justify-content: center;
    align-items: center;
	margin-right: -12px 0;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 15px;
}

input[type=submit]:hover {
	background-color: #45a049;
}
.button {
  background-color: #4CAF50; /* Green */
  border: none;
  color: white;
  padding: 16px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 14px;
  margin: 4px 2px;
  transition-duration: 0.4s;
  cursor: pointer;
}
label{
display:inline-block;
width:150px;
margin-left:40px;
text-align:left;
}



.button1 {
  background-color: white; 
  color: black; 
  border: 2px solid #4CAF50;
}
.button1:hover {
  background-color: #4CAF50;
  color: white;
}
</style>
</html>