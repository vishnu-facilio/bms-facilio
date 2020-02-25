package com.facilio.wms.message;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.wms.util.WmsApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
			int count = 0;
			if (data.getMessages() != null) {
				count = data.getMessages().size();
				if (count > 1) {
					data.setMessages(null);
				}
			}
			JSONObject jsonData = FieldUtil.getAsJSON(data);
			jsonData.put("count", count);
			addData("data", jsonData);
		}
	}
	
	public void send() throws Exception {
		System.out.println(this.getContent());
		System.out.println(this.toJson());
		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(AccountUtil.getCurrentOrg().getId());
		List<Long> recipients = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		WmsApi.sendPublishResponse(recipients, this);
	}

	public static void main(String[] args) throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(1,1);
		jsonObject.put(2,2);
		PublishMessage message = new PublishMessage();
		message.setData(jsonObject);
		PublishMessage message1 = new PublishMessage();
		message1.setData(jsonObject);

		PublishData data = new PublishData();
		data.setCommand(IoTMessageAPI.IotCommandType.CONFIGURE);
		data.setMessages(new ArrayList<>(Arrays.asList(message,message1)));
		data.setAcknowledgeTime(123);

		WmsPublishResponse wmsPublishResponse = new WmsPublishResponse();
		wmsPublishResponse.publish(data,null);
		}
	
}
