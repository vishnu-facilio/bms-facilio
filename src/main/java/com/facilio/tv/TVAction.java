package com.facilio.tv;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.fw.auth.CognitoUtil;
import com.facilio.screen.util.ScreenUtil;

public class TVAction extends FacilioAction {
	
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
			
			setResult("code", ScreenUtil.generateTVPasscode(info));
		} catch (Exception e) {
			setResponseCode(1);
			setMessage("passcode_generation_failed");
		}
		
		return SUCCESS;
	}
	
	public String validateCode() {
		
		try {
			Map<String, Object> codeObj = ScreenUtil.getTVPasscode(getCode());
			if (codeObj == null) {
				setResponseCode(1);
				setMessage("passcode_invalid");
				return ERROR;
			}
			else {
				Long connectedScreenId = (Long) codeObj.get("connectedScreenId");
				if (connectedScreenId != null && connectedScreenId > 0) {
					setResponseCode(0);
					setResult("status", "connected");
					
					String jwt = CognitoUtil.createJWT("id", "auth0", connectedScreenId + "", System.currentTimeMillis() + 24 * 60 * 60000, true);

	                HttpServletRequest request = ServletActionContext.getRequest();
	                HttpServletResponse response = ServletActionContext.getResponse();

	                Cookie cookie = new Cookie("fc.deviceToken", jwt);
	                cookie.setMaxAge(60 * 60 * 24 * 30);
	                cookie.setPath("/");
	                cookie.setHttpOnly(true);
	                cookie.setSecure(true);
	                response.addCookie(cookie);
	                
	                ScreenUtil.deleteTVPasscode(getCode());
					return SUCCESS;
				}
				else {
					long currentTime = System.currentTimeMillis();
					long expiryTime = (Long) codeObj.get("expiryTime");
					if (currentTime >= expiryTime) {
						ScreenUtil.deleteTVPasscode(getCode());
						
						setResponseCode(1);
						setMessage("passcode_expired");
						return ERROR;
					}
					else {
						setResponseCode(0);
						setResult("status", "not_connected");
						return SUCCESS;
					}
				}
			}
		} catch (Exception e) {
			setResponseCode(1);
			setMessage("passcode_error");
			return ERROR;
		}
	}
}
