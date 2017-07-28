<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<div class="row setup-header">
	<div class="col-lg-12 col-sm-12">
		<div class="col-lg-6 col-sm-6">
			<div class="pull-left">
				<div class="mini-header">
					<a href="setuphome"><s:property value="setup.settingTitle" /></a>
				</div>
				<div class="setup-header-text"><s:property value="setup.settingGroupTitle" /></div>
			</div>
		</div>
    </div>
</div>
<div class="row setup-list-container">
	<div class="row form-header" >
		<div class="col-sm-12" >
	  		<h4 class="pull-left"><s:property value="setup.settingViewTitle" /></h4>
	    	<div class="action-btn text-right">
	    		<s:iterator value="setup.actionButtons" status="rowstatus" var="actionButton">
	    			
	    			<s:if test="%{#actionButton.type == @com.facilio.bmsconsole.context.ActionButton$Type@SAVE}">
	    				<button type="button" class="btn btn-default save-btn <s:property value="#actionButton.className" />">
	    					<s:if test="%{#actionButton.iconClass != null || #actionButton.iconClass != ''}">
	    						<i class="fa <s:property value="#actionButton.iconClass" />" aria-hidden="true"></i>&nbsp;&nbsp;
	    					</s:if>
	    					<s:property value="#actionButton.name" />
	    				</button>
	    			</s:if>
	    			
	    			<s:if test="%{#actionButton.type == @com.facilio.bmsconsole.context.ActionButton$Type@CANCEL}">
	    				<button type="button" class="btn btn-default btn-circle <s:property value="#actionButton.className" />">
	    					<i class="fa <s:property value="#actionButton.iconClass" />"></i>
	    				</button>
	    			</s:if>
	    			
	    			<s:if test="%{#actionButton.type == @com.facilio.bmsconsole.context.ActionButton$Type@NORMAL}">
	    				<button type="button"  class="btn btn-default normal-btn <s:property value="#actionButton.className" />">
	    					<s:if test="%{#actionButton.iconClass != null || #actionButton.iconClass != ''}">
	    						<i class="fa <s:property value="#actionButton.iconClass" />" aria-hidden="true"></i>&nbsp;&nbsp;
	    					</s:if>
	    					<s:property value="#actionButton.name" />
	    				</button>
	    			</s:if>
	    			
	    		</s:iterator>
		 	</div>
	  	</div>
	</div>
	<div class="row setup-list-content">
	   <div class="col-md-12 col-sm-12 ">
	   		<tiles:insertAttribute name="body" />
	   </div>
	</div>
</div>