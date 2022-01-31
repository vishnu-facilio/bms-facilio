package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class EMailTemplate extends Template {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	private String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}


	public Long getFromID() {
		return fromID;
	}

	public void setFromID(Long fromID) {
		this.fromID = fromID;
	}

	private Long fromID;


	private String to;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	private String cc;

	private String bcc;

	public Boolean sendAsSeparateMail;

	public Boolean getSendAsSeparateMail() {
		return sendAsSeparateMail;
	}

	public void setSendAsSeparateMail(Boolean sendAsSeparateMail) {
		this.sendAsSeparateMail = sendAsSeparateMail;
	}
	public boolean isSendAsSeparateMail() {
		if (sendAsSeparateMail != null) {
			return sendAsSeparateMail.booleanValue();
		}
		return false;
	}


	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	private long bodyId;
	public long getBodyId() {
		return bodyId;
	}
	public void setBodyId(long bodyId) {
		this.bodyId = bodyId;
	}
	
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("sender", from);
		obj.put("to", to);
		obj.put("cc", cc);
		obj.put("bcc", bcc);
		obj.put("subject", subject);
		obj.put("message", message);
		obj.put("sendAsSeparateMail", sendAsSeparateMail);
		obj.put("html", isHtml());
		if (isHtml()) {
			obj.put("mailType", "html");
		}
		
		return obj;
	}
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.EMAIL.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.EMAIL;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	
	private Boolean html;
	public Boolean getHtml() {
		return html;
	}
	public void setHtml(Boolean html) {
		this.html = html;
	}
	public boolean isHtml() {
		if (html != null) {
			return html.booleanValue();
		}
		return false;
	}
	

	
}
