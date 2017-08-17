<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>

<div class="form-container form-content">
<form role="form" id="newPlanForm" method="post" onsubmit="return false;">

<div class="row">
<div class=" container-fluid text-center" >
		<div class="trial-info">
			<div class="caption">
	    		<div class="cur_plan">Current Plan : <span class="plan_name big-text">Estate</span> </div>
			</div>
			<div class="trial_exp">
	    		<label>Your trial will expire in 17 Days</label>
			</div>
			<div>Your account will get deactivated after the trial period. To continue using Facilio, please choose a plan below. </div>
		</div>
		
</div>
<BR><BR>
<div class="down_grade_warning">
  Note: If you downgrade a plan, you might lose some of the existing features
</div>
</div>
</form>
</div>
<table>
<div class="row">
<div class="col-sm-4" style="background-color:powderblue; height:350px; width: 20%;  padding-left: 80px;  margin-left: 80px;">

</div>
<div class="col-sm-4" style="background-color:powderblue; height:350px; width: 20%;  padding-left: 80px;  margin-left: 80px;">.col-sm-4</div>
<div class="col-sm-4" style="background-color:powderblue; height:350px; width: 20%;  padding-left: 80px;  margin-left: 80px;">.col-sm-4</div>
</div>
</table>
<style>

.cur_plan {
    font-size: 18px;
    text-transform: capitalize;
    line-height: 22px;
    display: inline-block;
    font-weight: 300;
}

.plan_name{
margin-left: 0;
}
.trial-info {
    text-align: center;
}
.big-text {
    line-height: 0;
    margin-left: 0;
    font-size: 16px;
    padding: 2px 0;
    }
 .trial_exp {
    margin: 15px 0;
    padding: 10px 20px;
    display: inline-block;
    background-color: #6DA749;
    border-radius: 18px;
    color: #fff;
    font-size: 18px;
    font-weight: 100;
}
 .caption {
    font-size: 16px;
    width: 100%;
    float: none !important;
    text-align: center;
}
</style>