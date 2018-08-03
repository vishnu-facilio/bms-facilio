package com.facilio.wms.message;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class Message 
{
	private MessageType messageType;
	private long from;
	private long to;
	private String namespace;
	private String action;
	private JSONObject content = new JSONObject();

	private static final Logger LOGGER = LogManager.getLogger(Message.class.getName());
	private static final String MESSAGE_TYPE = "messageType";
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String NAMESPACE = "namespace";
	private static final String ACTION = "action";
	private static final String CONTENT = "content";

	public Message(MessageType type) {
		this.messageType=type;
	}
	public Message() {
	}
	@Override
	public String toString() 
	{
		return super.toString();
	}

	public MessageType getMessageType() {
		return this.messageType;
	}

	public Message setMessageType(MessageType messageType) {
		this.messageType = messageType;
		return this;
	}
	
	public long getFrom() {
		return this.from;
	}

	public Message setFrom(long from) {
		this.from = from;
		return this;
	}
	
	public long getTo() {
		return this.to;
	}

	public Message setTo(long to) {
		this.to = to;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public Message setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public String getAction() {
		return action;
	}

	public Message setAction(String action) {
		this.action = action;
		return this;
	}

	public JSONObject getContent() {
		return content;
	}

	public Message setContent(JSONObject content) {
		this.content = content;
		return this;
	}
	
	public Message addData(String key, Object value) {
		this.content.put(key, value);
		return this;
	}
	
	public Object getData(String key) {
		return this.content.get(key);
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put(MESSAGE_TYPE, getMessageType().toString());
		object.put(FROM, getFrom());
		object.put(TO, getTo());
		object.put(NAMESPACE, getNamespace());
		object.put(ACTION, getAction());
		object.put(CONTENT, getContent());
		return object;
	}

	public static Message getMessage(JSONObject object){
		Message message = new Message();
		if (object.containsKey(MESSAGE_TYPE)) {
			message.setMessageType(MessageType.valueOf((String)object.get(MESSAGE_TYPE)));
		}
		if(object.containsKey(FROM)) {
			try {
				message.setFrom(Long.parseLong((String) object.get(FROM)));
			} catch (NumberFormatException e) {
				LOGGER.info("Exception while parsing message from ", e);
			}
		}
		if(object.containsKey(TO)) {
			try {
				message.setFrom(Long.parseLong((String) object.get(TO)));
			} catch (NumberFormatException e) {
				LOGGER.info("Exception while parsing message to ", e);
			}
		}

		if(object.containsKey(NAMESPACE)) {
			message.setNamespace((String)object.get(NAMESPACE));
		}

		if(object.containsKey(ACTION)) {
			message.setAction((String)object.get(ACTION));
		}

		if(object.containsKey(CONTENT)) {
			JSONParser parser = new JSONParser();
			try {
				message.setContent((JSONObject) parser.parse((String) object.get(TO)));
			} catch (ParseException e) {
				LOGGER.info("Exception while parsing message content ", e);
			}
		}

		return message;
	}
}