package com.facilio.bmsconsole.templates;

import com.facilio.emailtemplate.context.EMailStructure;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.Map;

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
		if (emailStructure != null) {
			return emailStructure.getSubject();
		}
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
		if (emailStructure != null) {
			return emailStructure.getMessage();
		}
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
	public List<TemplateAttachment> getAttachments() {
		if (emailStructure != null) {
			return emailStructure.getAttachments();
		}
		return super.getAttachments();
	}

	@Override
	protected void executeWorkflow(Map<String, Object> params, Map<String, Object> parameters) throws Exception {
		if (emailStructure != null) {
			emailStructure.executeWorkflow(params, parameters);
		}
		super.executeWorkflow(params, parameters);
	}

	@Override
	protected void executeUserWorkflow(Map<String, Object> params, Map<String, Object> parameters) throws Exception {
		if (emailStructure != null) {
			emailStructure.executeUserWorkflow(params, parameters);
		}
		super.executeUserWorkflow(params, parameters);
	}

	@Override
	public Boolean getIsAttachmentAdded() {
		if (emailStructure != null) {
			return emailStructure.getIsAttachmentAdded();
		}
		return super.getIsAttachmentAdded();
	}

	@Override
	protected void fetchAttachments() throws Exception {
		if (emailStructure != null) {
			emailStructure.fetchAttachments();
			return;
		}
		super.fetchAttachments();
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
		if (emailStructure != null){
			return emailStructure.getHtml();
		}
		return html;
	}
	public void setHtml(Boolean html) {
		this.html = html;
	}
	public boolean isHtml() {
		Boolean html = getHtml();
		if (html != null) {
			return html.booleanValue();
		}
		return false;
	}
}
