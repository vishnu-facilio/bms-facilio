<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>  <%@ taglib uri="/struts-tags" prefix="s" %>    
    
  
  
  <h4>  Import for <s:property value="%{#parameters.module}" /> </h4>
   
  <div> 
  <s:form namespace="/app/import" action="upload" enctype="multipart/form-data">
    <s:hidden value="%{#parameters.module}" />
    <s:file name="fileUpload"  />
    <input type="submit" name="upload" value="upload"/>
    </s:form>
    </div>
    