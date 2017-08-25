<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<div class="form-container form-content">

<div class="row well" >
<div class="pull-left">

<h2>Lead Assignment Rules</h2>
</div>

<h3 class="pull-right"> <span class= "glyphicon glyphicon-info-sign"></span> Help</h3>

</div>

<div class="row">
<div class="pull-left">
<label>Modules:
<input list="browsers" name="myBrowser" /></label>
<datalist id="browsers">
  <option value="Chrome">
  <option value="Firefox">
  <option value="Internet Explorer">
  <option value="Opera">
</datalist>
</div>
<div class="pull-right">
<button type="button" class="btn btn-primary" onclick="location.href='#newAssignmentRules' "><i class="fa fa-plus" aria-hidden="true"></i> Create Lead Assignment Rules</button>
</div>
</div>


<div class="row">
<div class="col-sm-12">
 <table class="table table-sm" style="margin-top:40px; border-top:1px solid gray;">
    <thead>
      <tr>
        <th>Rule Name</th>
        <th>Created By</th>
        <th> Created On</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><a href="#">USA Assignment Rule</a></td>
        <td>Magesh Veera</td>
        <td>08/16/2017</td>
      </tr>
      <tr>

    </tbody>
  </table>

  </div>
</div>
</div>
