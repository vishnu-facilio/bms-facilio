package com.facilio.serviceportal.actions; 
 
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.util.AuthenticationUtil;
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
    private static HashMap customdomains = null; 
	   
    @Override 
    public void init() { 
        super.init(); 
        initHost();
        
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
        	String domainName = request.getServerName();
        	String portalDomain = null;
			if(customdomains != null) { 
                String orgdomain = (String)customdomains.get(domainName); 
                if(orgdomain != null) { 
                    domainName = orgdomain+"."+ portalDomain; 
                } 
                log.info("Found a valid domain for custom domain for "+ domainName); 
            } 
            if(domainName != null) { 
                String[] domainArray = domainName.split("\\."); 
                if (domainArray.length > 2) { 
                    portalDomain = domainArray[0]; 
		        }
            }
            currentAccount = AuthenticationUtil.validateToken(request,true);
            request.setAttribute("iamAccount", currentAccount);
            request.setAttribute("isPortal", true);
            request.setAttribute("portalDomain", portalDomain);
         } 
        catch (Exception e){ 
            log.info("Exception occurred ", e); 
        } 
    } 
 
    private static String portalDomain = null; 
	 
    public static void setPortalDomain(String portalDomainName) { 
        portalDomain = portalDomainName; 
    } 
 
    public static String getPortalDomain(){ 
        return portalDomain; 
    } 
 
    private void initHost() { 
        try { 
            ServletContext context = ServletActionContext.getServletContext(); 
            if(customdomains == null && context != null) { 
                customdomains = (HashMap) context.getAttribute("customdomains"); 
            } 
        } catch (Exception e) { 
        	log.info("Exception occurred ", e); 
        } 
 
    }

} 
 