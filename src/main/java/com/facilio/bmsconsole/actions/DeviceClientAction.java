package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.actions.FacilioAuthAction;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.AESEncryption;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.impl.IamClient;
import com.facilio.iam.accounts.util.DCUtil;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;
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
				String scheme;
				//connected Device has been added user side.
				if (connectedDeviceId != null && connectedDeviceId > 0) {
					ConnectedDeviceContext connectedDevice = DevicesUtil.getConnectedDevice(connectedDeviceId);

					scheme= generateScheme(connectedDeviceId);

					String jwt = IAMUserUtil.createJWT("id", "auth0", connectedDeviceId + "", System.currentTimeMillis() + 24 * 60 * 60000);

	                ServletActionContext.getRequest();
	                HttpServletResponse response = ServletActionContext.getResponse();

	                Cookie cookie = new Cookie("fc.deviceTokenNew", jwt);
	                cookie.setMaxAge(60 * 60 * 24 * 30);
	                cookie.setPath("/");
	                cookie.setHttpOnly(true);
	             //   cookie.setSecure(true);
	                response.addCookie(cookie);

					Cookie cookie1 = new Cookie("fc.mobile.scheme", scheme);
					cookie1.setMaxAge(60 * 60 * 24 * 30);
					cookie1.setPath("/");
					cookie1.setHttpOnly(true);
					//   cookie.setSecure(true);
					response.addCookie(cookie1);

					FacilioAuthAction facilio = new FacilioAuthAction();
					JSONObject jsonObject = getMobileAuthJSON("token", jwt, "homePath", "/app/mobile/login", "domain", facilio.getDomain(), "baseUrl", getBaseUrl(connectedDevice));
					Cookie mobileTokenCookie = new Cookie("fc.mobile.idToken.facilio", new AESEncryption().encrypt(jsonObject.toJSONString()));
					setTempCookieProperties(mobileTokenCookie, false);

					response.addCookie(mobileTokenCookie);

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

	private JSONObject getMobileAuthJSON(String token, String authtoken, String homePath, String value, String domain, String Domain, String baseUrl, String BaseUrl) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(token, authtoken);
		jsonObject.put(homePath, value);
		jsonObject.put(domain, Domain);
		jsonObject.put(baseUrl, BaseUrl);
		return jsonObject;
	}

	private String getProtocol() {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		return RequestUtil.getProtocol(request);
	}

	private String generateScheme(long connectedDeviceId) throws Exception {
		String scheme;
		ConnectedDeviceContext connectedDevice = DevicesUtil.getConnectedDevice(connectedDeviceId);
		AccountUtil.setCurrentAccount(connectedDevice.getOrgId());
		DeviceContext device= DevicesAPI.getDevice(connectedDevice.getDeviceId());
		scheme = device.getDeviceTypeEnum().toString().toLowerCase();

		return scheme;
	}

	private void setTempCookieProperties(Cookie cookie, boolean authModel) {
		cookie.setMaxAge(60 * 60); // Make the cookie last an hour
		cookie.setPath("/");
		cookie.setHttpOnly(authModel);
		if (!(FacilioProperties.isDevelopment() || FacilioProperties.isOnpremise())) {
			cookie.setSecure(true);
		}
	}

	private String getBaseUrl(ConnectedDeviceContext connectedDevice) throws Exception {

		StringBuilder baseUrl = new StringBuilder(getProtocol()).append("://");
		List<AppDomain> appDomain = IAMAppUtil.getAppDomain(AppDomain.AppDomainType.FACILIO, connectedDevice.getOrgId());
		baseUrl.append(appDomain.stream().filter(i -> i.getDomainTypeEnum() == AppDomain.DomainType.DEFAULT).findAny().get().getDomain());
		String s = baseUrl.toString();
		return s;
	}

}
