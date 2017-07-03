<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>    

<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">New Controller</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
   <div class="col-lg-6">
   		<div class="panel-body">
   		<form role="form">
            <div class="form-group">
                <label>Device Name</label>
                <input class="form-control" name="controllerName">
                <p class="help-block">Name of the device.</p>
            </div>
            <div class="form-group">
               <label>Device Type</label>
               <select class="form-control" name="controllerType">
                   	<option value="-">-Select-</option>
                   	<option value="1">Distech Controls</option>
					<option value="2">Linux</option>
               </select>
            </div>
            <div class="form-group">
                <label>Data Source</label>
                <div class="radio">
                    <label>
                        <input type="radio" name="datasource" onclick="$(this).parent().parent().parent().next().show();" value="public" checked /> Public IP
                    </label>
                </div>
                <div class="radio">
                    <label>
                        <input type="radio" name="datasource" onclick="$(this).parent().parent().parent().next().hide();" value="mqtt" /> MQTT Client<br>
                    </label>
                </div>
            </div>
            <div class="form-group">
                <label>Public IP</label>
                <input class="form-control" name="publicip">
            </div>
            <div class="form-group">
                <label>Time Interval</label>
                <input class="form-control" name="timeinterval">
            </div>
            <button type="reset" class="btn btn-outline btn-default" onclick="location.href='#device';">Go Back</button>
            <button type="submit" class="btn btn-outline btn-primary">Save</button>
		</form>
		</div>
	</div>
</div>