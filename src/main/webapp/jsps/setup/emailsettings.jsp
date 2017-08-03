<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="container adminContent">
<div class="row">
<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-left"> <i class="fa fa-cog" aria-hidden="true"></i>
<h3 class="admin">Admin</h3>
<a href="#" class="downArrow"><i class="fa fa-caret-down" aria-hidden="true"></i></a> </div>
<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 text-right"> <a href="#" class="helpdeskBtn">New Helpdesk Email</a> </div>
</div>
</div>
<div class="container-fluid">
<div class="row">
<div class="border"></div>
</div>
</div>
<div class="container">
<div class="row">
<div class="heading1">
<h3>Email Settings</h3>
<p>List of all helpdesk email addresses</p>
</div>
</div>
</div>
<div class="container">
<div class="row">
<div class="heading2">
<h3 class="leftHeading">Global support Emails</h3>
<p class="rightText"><a href="#">Add Bcc</a> <i class="fa fa-question-circle-o" data-toggle="tooltip" title="This email address will be included automatically in the bcc field for all communications." aria-hidden="true"></i></p>
</div>
<div class="borderTop"></div>
<div class="content">
<div class="row">
<div class="col-xs-12 col-sm-1 col-md-1 col-lg-1">
<p></p>
<i class="fa fa-star" aria-hidden="true"></i> </div>
<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3">
<div class="facilio">
<p class="titleStrong">facilio</p>
<p class="mailId">helpdesk@facilio1234.fresh
service.com</p>
</div>
</div>
<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3">
<div class="forward">
<p class="titleLight">Forwarded to:</p>
<p class="mailId">helpdesk@facilio1234.fresh
service.com</p>
</div>
</div>
<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3">
<p class="titleLight">Group:</p>
<p>-</p>
</div>
<div class="col-xs-12 col-sm-2 col-md-2 col-lg-2 text-right">
<div class="btns"> <a href="#" class="editBtn">Edit</a> <a href="#" class="delBtn"><i class="fa fa-trash-o" aria-hidden="true"></i></a> </div>
</div>
</div>
</div>
<div class="borderBottom"></div>
</div>
</div>
<div class="container question">
<div class="row">
<div class="agent">
<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9">
<h3 class="titleAgent">Use Agent names for Personalized Email Replies ?</h3>
<p class="agentContent">If YES then agent replies will have agent's name as the sender name (Email address will still be your Helpdesk Email address). For example 'zulfikar &lt;helpdesk@facilio1234.freshservice.com&gt;'. </p>
</div>
<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 text-right">
<div class="yesNo">
<input type="checkbox" id="toggle-two">
<script>
$(function() {
$('#toggle-two').bootstrapToggle({
on: 'Yes',
off: 'No'
});
})
</script></div></div>
</div>
</div>
<div class="row">
<div class="reply">
<p class="titleReply">
<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9">
<h3 class="titleAgent">Use "Reply-to" email address to create requestor contacts</h3>
<p class="replyContent">Note: If disabled, requestor will be created using 'From' address in email.</p>
</div>
<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 text-right">
<div class="yesNo1">
<input type="checkbox" id="toggle-two1">
<script>
$(function() {
$('#toggle-two1').bootstrapToggle({
on: 'Yes',
off: 'No'
});
})
</script>
</div>
</div>
</div>
</div>
</div>