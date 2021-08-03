package com.facilio.bmsconsole.actions;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.impl.IamClient;
import com.facilio.iam.accounts.util.DCUtil;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.service.FacilioService;


public class DeviceClientAction extends FacilioAction{
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
			
			//TO DO , DO NOT SET THREADLOCAL IN DEVICE URL , even if logged in ,once done remove runasservice wrap			
			setResult("code", FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.generateDevicePasscode((info))));
		} catch (Exception e) {
			System.out.print(e);
			throw new IllegalArgumentException("passcode_generation_failed");
		}
		
		return SUCCESS;
	}
public String validateCode() {
		
		try {
			Map<String, Object> codeObj = IamClient.getDevicePasscode(getCode());
			if (codeObj == null) {
				throw new IllegalArgumentException("passcode_invalid");
			}
			else {
				Long connectedDeviceId = (Long) codeObj.get("connectedDeviceId");
				//connected Device has been added user side.
				if (connectedDeviceId != null && connectedDeviceId > 0) {
					
					String jwt = IAMUserUtil.createJWT("id", "auth0", connectedDeviceId + "", System.currentTimeMillis() + 24 * 60 * 60000);

	                ServletActionContext.getRequest();
	                HttpServletResponse response = ServletActionContext.getResponse();

	                Cookie cookie = new Cookie("fc.deviceTokenNew", jwt);
	                cookie.setMaxAge(60 * 60 * 24 * 30);
	                cookie.setPath("/");
	                cookie.setHttpOnly(true);
	             //   cookie.setSecure(true);
	                response.addCookie(cookie);
	                IamClient.deleteDevicePassCode(getCode());
	                
	                setResponseCode(0);
					setResult("status", "connected");
					setResult("token", jwt);
					setResult("dc", DCUtil.getMainAppDomain(Integer.valueOf((long) codeObj.get("dc") + "")));
	               
					return SUCCESS;
				}
				else {
					long currentTime = System.currentTimeMillis();
					long expiryTime = (Long) codeObj.get("expiryTime");
					if (currentTime >= expiryTime) {
						  IamClient.deleteDevicePassCode(getCode());
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
