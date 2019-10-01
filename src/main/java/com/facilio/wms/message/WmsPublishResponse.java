package com.facilio.wms.message;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.modules.FieldUtil;
import com.facilio.wms.util.WmsApi;

public class WmsPublishResponse extends Message {
	
	private static final Logger LOGGER = LogManager.getLogger(WmsPublishResponse.class.getName());

	public WmsPublishResponse() {
		setMessageType(MessageType.PUBLISH);
		setNamespace("publishdata");
	}
	
	public WmsPublishResponse publish(PublishData data, JSONObject info) throws Exception {
		addJsonData(data);
		addData("info", info);
		setAction("publish");
		send();
		return this;
	}
	
	public WmsPublishResponse publishFailure(PublishData data) throws Exception {
		addJsonData(data);
		setAction("publishFailure");
		send();
		return this;
	}
	
	private void addJsonData(PublishData data) throws Exception {
		if (data != null) {
			int count = data.getMessages().size();
			if (count > 1) {
				data.setMessages(null);
			}
			JSONObject jsonData = FieldUtil.getAsJSON(data);
			jsonData.put("count", count);
			addData("data", jsonData);
		}
	}
	
	public void send() throws Exception {
		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
		List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		
		WmsApi.sendPublishResponse(recipients, this);
	}
	
}
