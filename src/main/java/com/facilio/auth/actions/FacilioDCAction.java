package com.facilio.auth.actions;

import java.util.Map;

import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.service.FacilioService;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

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
		int dc = IAMUserUtil.findDCForUser(userName, groupType);
		setData("dc", dc);
		
		return SUCCESS;
	}
	
	private Map<String, Object> user;
	public String addUser() throws Exception {
		IAMUserUtil.addDCLookup(user);
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

	public String deleteDevicePasscode() throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.DEFAULT_SERVICE,() ->  DevicesUtil.deleteDevicePasscode(getDeviceCode()));
		setData("result", "success");
		return SUCCESS;
	}

}
