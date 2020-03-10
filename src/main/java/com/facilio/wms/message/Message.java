package com.facilio.wms.message;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.wms.endpoints.LiveSession.LiveSessionType;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class Message 
{
	private LiveSessionType sessionType = null;
	private MessageType messageType;
	private Long from;
	private Long to;
	private String namespace;
	private String action;
	private JSONObject content = new JSONObject();
	private Long timestamp;
	private Long orgId;

	private static final Logger LOGGER = LogManager.getLogger(Message.class.getName());
	private static final String SESSION_TYPE = "sessionType";
	private static final String MESSAGE_TYPE = "messageType";
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String NAMESPACE = "namespace";
	private static final String ACTION = "action";
	private static final String CONTENT = "content";
	private static final String TIMESTAMP = "timestamp";

	public Message(MessageType type) {
		this.messageType=type;
	}
	public Message() {
	}
	@Override
	public String toString() {
		return toJson().toString();
	}

	public MessageType getMessageType() {
		return this.messageType;
	}

	public Message setMessageType(MessageType messageType) {
		this.messageType = messageType;
		return this;
	}
	
	public Message setMessageType(String messageType) {
		this.messageType = MessageType.valueOf(messageType);
		return this;
	}
	
	public LiveSessionType getSessionType() {
		return this.sessionType;
	}

	public Message setSessionType(LiveSessionType sessionType) {
		this.sessionType = sessionType;
		return this;
	}
	
	public Long getFrom() {
		return this.from;
	}

	public Message setFrom(Long from) {
		this.from = from;
		return this;
	}
	
	public Long getOrgId() {
		return this.orgId;
	}

	public Message setOrgId(Long orgId) {
		this.orgId = orgId;
		return this;
	}
	
	public Long getTimestamp() {
		return this.timestamp;
	}

	public Message setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	public Long getTo() {
		return this.to;
	}

	public Message setTo(Long to) {
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
		if(getSessionType() != null) {
			object.put(SESSION_TYPE, getSessionType().toString());
		}
		if(getMessageType() != null) {
			object.put(MESSAGE_TYPE, getMessageType().toString());
		}
		if(getFrom() != null) {
			object.put(FROM, getFrom());
		}
		if(getTo() != null) {
			object.put(TO, getTo());
		}
		if(getNamespace() != null) {
			object.put(NAMESPACE, getNamespace());
		}
		if(getAction() != null) {
			object.put(ACTION, getAction());
		}
		if(getContent() != null) {
			object.put(CONTENT, getContent());
		}
		if(getTimestamp() != null) {
			object.put(TIMESTAMP, System.currentTimeMillis());
		}
		return object;
	}

	public static Message getMessage(JSONObject object){
		Message message = new Message();
		if (object.containsKey(MESSAGE_TYPE)) {
			message.setMessageType(MessageType.valueOf((String)object.get(MESSAGE_TYPE)));
		}
		if (object.containsKey(SESSION_TYPE)) {
			message.setSessionType(LiveSessionType.valueOf((String)object.get(SESSION_TYPE)));
		}
		if(object.containsKey(FROM)) {
			try {
				message.setFrom((Long) object.get(FROM));
			} catch (NumberFormatException e) {
				LOGGER.info("Exception while parsing message from ", e);
			}
		}
		if(object.containsKey(TO)) {
			try {
				message.setTo((Long) object.get(TO));
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
			message.setContent((JSONObject) object.get(CONTENT));
		}
		
		if(object.containsKey(TIMESTAMP)) {
			try {
				message.setTimestamp((Long) object.get(TIMESTAMP));
			} catch (NumberFormatException e) {
				LOGGER.info("Exception while parsing message to ", e);
			}
		}

		return message;
	}

}
