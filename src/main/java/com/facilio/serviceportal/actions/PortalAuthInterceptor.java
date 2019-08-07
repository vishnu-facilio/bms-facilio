package com.facilio.serviceportal.actions; 
 
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.util.AuthenticationUtil;
import com.iam.accounts.util.IAMOrgUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor; 
 
 
public class PortalAuthInterceptor extends AbstractInterceptor { 
    /** 
     *  
     */ 
    private static final long serialVersionUID = 1L; 
    private static Logger logger = Logger.getLogger(PortalAuthInterceptor.class.getName()); 
    private static org.apache.log4j.Logger log = LogManager.getLogger(PortalAuthInterceptor.class.getName()); 
 
    
    @Override 
    public void init() { 
        super.init(); 
    } 
     
    @Override 
    public String intercept(ActionInvocation arg0) throws Exception { 
        try { 
            intercept0(); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, "error in portal auth interceptor", e); 
            return Action.LOGIN; 
        } 
        try { 
            return arg0.invoke(); 
        } catch (Exception e) { 
            logger.log(Level.SEVERE, "error thrown from action class", e); 
            throw e; 
        } 
    } 
 
    private void intercept0() { 
        HttpServletRequest request = ServletActionContext.getRequest(); 
        IAMAccount currentAccount = null; 
        try { 
            currentAccount = AuthenticationUtil.validateToken(request,true);
            request.setAttribute("iamAccount", currentAccount);
            request.setAttribute("isPortal", true);
         } 
        catch (Exception e){ 
            log.info("Exception occurred ", e); 
        } 
    } 
 
 
} 
 