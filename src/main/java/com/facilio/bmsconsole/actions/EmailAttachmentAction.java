package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateFileFieldContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class EmailAttachmentAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	
	private long templateId = -1;
	
	private String moduleName;
	
	private List<File> attachment;
	
	private List<String> attachmentFileName;
	
	private List<String> urlList;
	


	public List<Long> getAttachmentIds() {
		return attachmentIds;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	public void setAttachmentIds(List<Long> attachmentIds) {
		this.attachmentIds = attachmentIds;
	}

	private List<String> attachmentContentType;
	
	private AttachmentType attachmentType;
	
	private List<Long> attachmentIds;

	public List<File> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<File> attachment) {
		this.attachment = attachment;
	}

	public List<String> getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(List<String> attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public List<String> getAttachmentContentType() {
		return attachmentContentType;
	}

	public void setAttachmentContentType(List<String> attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}

	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		FacilioChain getListChain = TransactionChainFactory.getAttachmentsListTranslationChain();
		getListChain.execute(context);
		
		List<TemplateFileContext> attachmentList = (List<TemplateFileContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);		
		List<TemplateUrlContext> attachmentUrlList = (List<TemplateUrlContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_URL_LIST);
		List<TemplateFileFieldContext> attachmentFileFieldslList = (List<TemplateFileFieldContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_FIELDID_LIST);
		
		setResult("attachments", attachmentList);
		setResult("attachmentUrlList", attachmentUrlList);
		setResult("attachmentFileFieldslList", attachmentFileFieldslList);
		
		return SUCCESS;
	}
	
	public String delete() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, attachmentIds);
		
		FacilioChain getDeleteChain = TransactionChainFactory.getDeleteAttachmentsListChain();
		getDeleteChain.execute(context);
		
		return SUCCESS;
	}
	

}
