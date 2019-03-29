package com.facilio.wms.message;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.wms.util.WmsApi;

import java.util.List;
import java.util.stream.Collectors;

public class WmsPublishResponse extends Message {

	public WmsPublishResponse() {
		setMessageType(MessageType.PUBLISH);
		setNamespace("publishdata");
	}
	
	public WmsPublishResponse publish(PublishData data) throws Exception {
		addData("response", data);
		setAction("publish");
		send();
		return this;
	}
	
	public void send() throws Exception {
		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
		List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		
		WmsApi.sendPublishResponse(recipients, this);
	}
	
}
