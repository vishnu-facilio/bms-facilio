package com.facilio.wms.message;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
	
	public WmsPublishResponse publish(PublishData data, long userId) throws Exception {
		addData("response", data);
		setAction("publish");
		send(userId);
		return this;
	}
	
	public void send(long userId) throws Exception {
		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
		List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		LOGGER.info("User Ids for publishing data: " + recipients);
		
		WmsApi.sendPublishResponse(Collections.singletonList(userId), this);
	}
	
}
