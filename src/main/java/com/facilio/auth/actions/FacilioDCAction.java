package com.facilio.auth.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.service.FacilioService;
import com.facilio.v3.V3Action;

import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

@Log4j
@Getter @Setter
public class FacilioDCAction extends V3Action {
	
	private String userName;
	
	private GroupType groupType;
	public void setGroupType(int groupTypeInt) {
		this.groupType = GroupType.valueOf(groupTypeInt);
	}

	private String deviceCode;

	private long connectedDeviceId;

	private int dc;
	
	public String dclookup() throws Exception {
		try {
			int dc = IAMUserUtil.findDCForUser(userName, groupType);
			setData("dc", dc);
		} catch (Exception ex) {
			return "invaliduser";
		}

		return SUCCESS;
	}
	
	private Map<String, Object> user;
	public String addUser() throws Exception {
		try {
			IAMUserUtil.addDCLookup(user);
			LOGGER.info("User added in DC_Lookup " + user.get("userName"));
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof IllegalArgumentException) {
				throw new RESTException(ErrorCode.USER_ALREADY_EXISTS, ex.getTargetException().getMessage());
			} else {
				throw ex;
			}
		}
		setData("result", "success");

		return SUCCESS;
	}

	public String getDeviceCodeInfo() throws Exception {
		Map<String, Object> deviceCodeRow = FacilioService
				.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() -> DevicesUtil.getValidDeviceCodeRow(deviceCode));
		setData("deviceCodeInfo", deviceCodeRow);
		return SUCCESS;
	}

	public String markCodeAsConnected() throws Exception {
		FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.markCodeAsConnected(deviceCode,connectedDeviceId, dc));
		setData("result", "success");
		return SUCCESS;
	}

	public String getDevicePasscode() throws Exception {
		Map<String, Object> codeObj = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.getDevicePasscodeRow(getDeviceCode()));
		setData("deviceCodeInfo", codeObj);
		return SUCCESS;
	}

	public String deleteDevicePassCode() throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.deleteDevicePasscode(getDeviceCode()));
		setData("result", "success");
		return SUCCESS;
	}

	public String deleteUserFromDCLookup() throws Exception {
		IAMUtil.getUserBean().deleteDCLookup(getUserName(), getGroupType());
		setData("result", "success");
		return SUCCESS;
	}
	public String getConnectedDeviceForId() throws Exception {
		ConnectedDeviceContext connectedDevice = DevicesUtil.getConnectedDevice(connectedDeviceId);
		AccountUtil.setCurrentAccount(connectedDevice.getOrgId());
		DeviceContext device = DevicesAPI.getDevice(connectedDevice.getDeviceId());
		String scheme = device.getDeviceTypeEnum().toString().toLowerCase().replace("_","");
		Map<String, Object> deviceObj = new HashMap<>();
		deviceObj.put("scheme", scheme);
		deviceObj.put("orgId", connectedDevice.getOrgId());
		setData("deviceObj", deviceObj);
		return SUCCESS;
	}


}
