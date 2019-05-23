package com.facilio.wms.message;

import java.util.List;
import java.util.stream.Collectors;

import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.wms.util.WmsApi;

public class WmsPublishResponse extends Message {
	
	private static final Logger LOGGER = LogManager.getLogger(WmsPublishResponse.class.getName());

	public WmsPublishResponse() {
		setMessageType(MessageType.PUBLISH);
		setNamespace("publishdata");
	}
	
	public WmsPublishResponse publish(PublishData data, JSONObject info) throws Exception {
		addData("data", FieldUtil.getAsJSON(data));
		addData("info", info);
		setAction("publish");
		send();
		return this;
	}
	
	public WmsPublishResponse publishFailure(PublishData data) throws Exception {
		addData("data", FieldUtil.getAsJSON(data));
		setAction("publishFailure");
		send();
		return this;
	}
	
	public void send() throws Exception {
		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
		List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		
		WmsApi.sendPublishResponse(recipients, this);
	}
	
}
