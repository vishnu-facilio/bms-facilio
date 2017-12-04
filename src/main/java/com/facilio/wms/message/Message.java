package com.facilio.wms.message;

import org.json.simple.JSONObject;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class Message 
{
	private MessageType messageType;
	private long from;
	private long to;
	private JSONObject content = new JSONObject();

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
}