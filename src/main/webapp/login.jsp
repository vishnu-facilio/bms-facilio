<html>

<head>
<link href="css/styles.css" rel="stylesheet" type="text/css">
<script src="js/aws/aws-cognito-sdk.min.js"></script>
    <script src="js/aws/amazon-cognito-identity.min.js"></script>
        <script src="js/login.js"></script>
    
</head>
<body background="https://mediastream.jumeirah.com/webimage/heroactual//globalassets/documents/jad/jumeirah-at-etihad-towers-5-year-hero-01.jpg">
<div class="product-header">
  <div class="toolbar" style="width:1500px;margin-top : 0;">
      <ul>
        <li><a href="/features/">Features</a></li>
        <li><a href="/extensions/">Extensions</a></li>
        <li><a href="/learn/">Learn</a></li>
        <li><a href="/community/">Community</a></li>
        <li><a href="/support/faq/">Support</a></li>
      </ul>
  </div>
</div>

<div id="login" style="position:absolute;margin-top:60;margin-left=250px;padding-left:180px">

<div id="signupform"  style="display:none;position:absolute;top:0;left=50px;padding-left:50px;width:300px">
<form action='CheckServlet' method="post">
  <div class="container">
    <label><b>Email</b></label>
    <input type="text" placeholder="Enter Email" name="email" required>
	<label><b>Password</b></label>
    <input type="password" placeholder="Enter Password" name="psw" required>
  <label><b>Confirm Password</b></label>
    <input type="password" placeholder="Re Type Password" name="psw" required>
    <p>By creating an account you agree to our <a href="#">Terms &nbsp; Privacy</a>.</p>
    <div class="clearfix">
      <button type="submit"  class="cancelbtn" >Cancel</button>
      <input type="submit" class="signupbtn" value="Sign up">
    </div>
  </div>
</form></div>
<div id="signinform"  style="display:block;position:absolute;top:10;left=50px;padding-left:50px;width:300px">
<form action="CheckServlet" method="post">
  <div class="container">
    <label><b>Email</b></label>
    <input type="text" placeholder="Enter Email" name="email" required>

    <label><b>Password</b></label>
    <input type="password" placeholder="Enter Password" name="psw" required>

    
    <input type="checkbox" checked="checked"> Remember me
    
    <div class="clearfix">
      <button type="button"  class="cancelbtn" onClick="javascript:displaysignup()">Sign up</button>
      <input type="submit" class="signupbtn" value= "Sign In">
    </div>
  </div>
</form>
</div>
</div>
</body>
</html>
