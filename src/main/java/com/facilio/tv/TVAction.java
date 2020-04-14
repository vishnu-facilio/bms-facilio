package com.facilio.tv;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.service.FacilioService;

public class TVAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String generateCode() {
		
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent != null ? userAgent : "";
			String ipAddress = request.getHeader("X-Forwarded-For");
			ipAddress = (ipAddress == null || "".equals(ipAddress.trim())) ? request.getRemoteAddr() : ipAddress;
			
			JSONObject info = new JSONObject();
			info.put("userAgent", userAgent);
			info.put("ipAddress", ipAddress);
			
			setResult("code", FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.generateTVPasscode(info)));
		} catch (Exception e) {
			throw new IllegalArgumentException("passcode_generation_failed");
		}
		
		return SUCCESS;
	}
	
	public String validateCode() {
		
		try {
			Map<String, Object> codeObj = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getTVPasscode(getCode()));
			if (codeObj == null) {
				throw new IllegalArgumentException("passcode_invalid");
			}
			else {
				Long connectedScreenId = (Long) codeObj.get("connectedScreenId");
				if (connectedScreenId != null && connectedScreenId > 0) {
					setResponseCode(0);
					setResult("status", "connected");
					
					String jwt = IAMUserUtil.createJWT("id", "auth0", connectedScreenId + "", System.currentTimeMillis() + 24 * 60 * 60000);

	                ServletActionContext.getRequest();
	                HttpServletResponse response = ServletActionContext.getResponse();

	                Cookie cookie = new Cookie("fc.deviceToken", jwt);
	                cookie.setMaxAge(60 * 60 * 24 * 30);
	                cookie.setPath("/");
	                cookie.setHttpOnly(true);
	             //   cookie.setSecure(true);
	                response.addCookie(cookie);
	                
	                RemoteScreenContext remoteScreen = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getRemoteScreen(connectedScreenId));
	                
	                if (remoteScreen.getScreenId() != null) {
	                	
		                Long siteId = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getScreen(remoteScreen.getScreenId()).getSiteId());
		                if (siteId != null) {
			             Cookie sitecookie = new Cookie("fc.currentSite", String.valueOf(siteId));
	                	sitecookie.setMaxAge(60 * 60 * 24 * 30);
	                	sitecookie.setPath("/");
			            response.addCookie(sitecookie);
		                }
	                }	                


	                FacilioService.runAsService(() ->  ScreenUtil.deleteTVPasscode(getCode()));
	               
					return SUCCESS;
				}
				else {
					long currentTime = System.currentTimeMillis();
					long expiryTime = (Long) codeObj.get("expiryTime");
					if (currentTime >= expiryTime) {
						  FacilioService.runAsService(() ->  ScreenUtil.deleteTVPasscode(getCode()));
			              throw new IllegalArgumentException("passcode_expired");
					}
					else {
						setResponseCode(0);
						setResult("status", "not_connected");
						return SUCCESS;
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("passcode_error");
		}
	}
}
