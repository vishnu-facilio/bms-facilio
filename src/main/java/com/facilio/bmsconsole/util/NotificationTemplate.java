package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.ActivityType;

public class NotificationTemplate {
	
	public static JSONObject getFormattedMessage(NotificationContext notification) throws Exception {
		
		JSONObject templateJson = getMessageTemplate(notification.getNotificationType());
		if (templateJson == null) {
			return null;
		}
		
		Map<String, Object> placeholders = new HashMap<>();
		if (notification.getActorId() > 0) {
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getUserBean().getUser(notification.getActorId())), placeholders);
		}
		CommonCommandUtil.appendModuleNameInKey(null, "info", notification.getInfoJson(), placeholders);
		
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(StrSubstitutor.replace(templateJson, placeholders));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static JSONObject getMessageTemplate(ActivityType notificationType) {
		
		if (notificationType == null) {
			return null;
		}
		
		JSONObject json = new JSONObject();
		
		switch (notificationType) {
		case ASSIGN_TICKET:
			json.put("content", "<b>${actor.name}</b> assigned the workorder <b>(#${info.id}) ${info.title}</b> to you.");
			json.put("link", "https://fazilio.com");
			break;

		default:
			break;
		}
		return json;
	}
}
