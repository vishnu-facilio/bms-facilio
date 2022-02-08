package com.facilio.bmsconsole.templates;

import com.facilio.emailtemplate.context.EMailStructure;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class EMailTemplate extends Template {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

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

	private String cc;
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}

	private String bcc;
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

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

	private long emailStructureId = -1;
	public long getEmailStructureId() {
		return emailStructureId;
	}
	public void setEmailStructureId(long emailStructureId) {
		this.emailStructureId = emailStructureId;
	}

	private EMailStructure emailStructure;
	public EMailStructure getEmailStructure() {
		return emailStructure;
	}
	public void setEmailStructure(EMailStructure emailStructure) {
		this.emailStructure = emailStructure;
	}

	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("sender", from);
		obj.put("to", to);
		obj.put("cc", cc);
		obj.put("bcc", bcc);
		obj.put("subject", getSubject());
		obj.put("message", getMessage());
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
