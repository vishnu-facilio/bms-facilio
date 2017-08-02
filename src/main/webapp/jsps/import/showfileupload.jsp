<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

    <%@ taglib uri="/struts-tags" prefix="s" %>    
    
  
  
  <h4>  Import for <s:property value="%{#parameters.module}" /> </h4>
   
   <form>
    <input type="file" name="upload">
    <input type="button" name="upload" value="upload"/>
    </form>