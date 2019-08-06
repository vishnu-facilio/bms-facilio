package com.facilio.bmsconsole.interceptors;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.iam.accounts.util.IAMUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DataSourceInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
//		IAMAccount iamAccount = (IAMAccount) invocation.getStack().findValue("iamAccount");
		HttpServletRequest request = ServletActionContext.getRequest();
		IAMAccount iamAccount = (IAMAccount) request.getAttribute("iamAccount");
		
		if (iamAccount != null) {
//			HttpServletRequest request = ServletActionContext.getRequest();

			String currentOrgDomain = FacilioCookie.getUserCookie(request, "fc.currentOrg");
			if (currentOrgDomain == null) {
				currentOrgDomain = request.getHeader("X-Current-Org"); 
			}
			
			Organization organization = null;
			if (StringUtils.isNotBlank(currentOrgDomain)) {
				organization = IAMUtil.getOrgBean().getOrgv2(currentOrgDomain);
			}
			else {
				organization = IAMUtil.getUserBean().getDefaultOrgv2(iamAccount.getUser().getUid());
			}
			
			iamAccount.setOrg(organization);
			
//			Account currentAccount = new Account(organization, new User(iamAccount.getUser()));
			
//			long uid = currentAccount.getUser().getUid();
//			User user = AccountUtil.getUserBean().getUser(currentAccount.getOrg().getOrgId(), uid);
//			if (user == null) {
//				throw new IllegalArgumentException("User doesn't exists in org");
//			}
//			currentAccount.setUser(user);
//			
//			if (request.getRequestURL().indexOf("/admin") != -1) {
//				if (currentAccount != null) {
//					String useremail = currentAccount.getUser().getEmail();
//					if (! useremail.endsWith(AwsUtil.getConfig("admin.domain"))) {
//						LOGGER.log(Level.SEVERE, "you are not allowed to access this page from");
//						return Action.LOGIN;
//					}
//				}
//			}
			try {
				AccountUtil.setReqUri(request.getRequestURI());
	            AccountUtil.setRequestParams(request.getParameterMap());
			} catch (Exception e) {
				System.out.println("exception code 154");

				throw e;
			}
		}
		
		
		return invocation.invoke();
	}

}
