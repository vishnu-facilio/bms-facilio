package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class NotificationTemplate {
	private static org.apache.log4j.Logger log = LogManager.getLogger(NotificationTemplate.class.getName());

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
			return (JSONObject) parser.parse(StringSubstitutor.replace(templateJson, placeholders));
		} catch (ParseException e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	private static JSONObject getMessageTemplate(EventType notificationType) {
		
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
