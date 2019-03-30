package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class AttachmentAction  extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(AttachmentAction.class.getName());
	private String module;
	public String getModule() {
		return this.module;
	}
	
	public void setModule(String module) {
		this.module = module;
	}
	
	private long recordId = -1;
	public long getRecordId() {
		return this.recordId;
	}
	
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private long id = -1;
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	private List<File> attachment;
	public List<File> getAttachment() {
		return attachment;
	}
	public void setAttachment(List<File> attachment) {
		this.attachment = attachment;
	}
	
	private List<String> attachmentContentType;
	public List<String> getAttachmentContentType() {
		return attachmentContentType;
	}
	public void setAttachmentContentType(List<String> attachmentContentType) {
		this.attachmentContentType = attachmentContentType;
	}
	
	private List<String> attachmentFileName;
	public List<String> getAttachmentFileName() {
		return attachmentFileName;
	}
	public void setAttachmentFileName(List<String> attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}
	
	private List<Long> attachmentId;
	public List<Long> getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(List<Long> attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	public String execute() {
		return SUCCESS;
	}
	
	private AttachmentType attachmentType;
	public int getAttachmentType() {
		if(attachmentType != null) {
			return attachmentType.getIntVal();
		}
		return -1;
	}
	public void setAttachmentType(int attachmentType) {
		this.attachmentType = AttachmentType.getType(attachmentType);
	}

	public String addAttachment() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);
		
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachment);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachmentFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachmentContentType);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
 		
		Chain addAttachmentChain = FacilioChainFactory.getAddAttachmentChain();
		addAttachmentChain.execute(context);
		
		List<AttachmentContext> attachmentList = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
		setAttachments(attachmentList);
		
		if (attachmentList != null && !attachmentList.isEmpty()) {
			if (module.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				attachmentList.get(0).setAttachmentModule(FacilioConstants.ContextNames.ASSET_LIST);
				attachmentList.get(0).setRecordId(recordId);
			}
			if (module.equals(FacilioConstants.ContextNames.INVENTORY_ATTACHMENTS)) {
				attachmentList.get(0).setAttachmentModule(FacilioConstants.ContextNames.INVENTORY_LIST);
				attachmentList.get(0).setRecordId(recordId);
			}
			if (module.equals(FacilioConstants.ContextNames.ITEM_TYPES_ATTACHMENTS)) {
				attachmentList.get(0).setAttachmentModule(FacilioConstants.ContextNames.ITEM_TYPES_LIST);
				attachmentList.get(0).setRecordId(recordId);
			}
			if (module.equals(FacilioConstants.ContextNames.TOOL_TYPES_ATTACHMENTS)) {
				attachmentList.get(0).setAttachmentModule(FacilioConstants.ContextNames.TOOL_TYPES_LIST);
				attachmentList.get(0).setRecordId(recordId);
			}
			if (module.equals(FacilioConstants.ContextNames.STORE_ROOM_ATTACHMENTS)) {
				attachmentList.get(0).setAttachmentModule(FacilioConstants.ContextNames.STORE_ROOM_LIST);
				attachmentList.get(0).setRecordId(recordId);
			}
		}
		return SUCCESS;
	}

	private List<AttachmentContext> attachments;
	public List<AttachmentContext> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<AttachmentContext> attachments) {
		this.attachments = attachments;
	}
	
	public String attachmentList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
		context.put(FacilioConstants.ContextNames.RECORD_ID, this.recordId);
		
		Chain getAttachmentsChain = FacilioChainFactory.getAttachmentsChain();
		getAttachmentsChain.execute(context);
		
		List<AttachmentContext> attachmentList = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
		if (attachmentList != null && !attachmentList.isEmpty()) {
			if (module.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				attachmentList.forEach(attachment -> {
					attachment.setAttachmentModule(FacilioConstants.ContextNames.ASSET_LIST);
					attachment.setRecordId(recordId);
				});
			}
			else if (module.equals(FacilioConstants.ContextNames.INVENTORY_ATTACHMENTS)) {
				attachmentList.forEach(attachment -> {
					attachment.setAttachmentModule(FacilioConstants.ContextNames.INVENTORY_LIST);
					attachment.setRecordId(recordId);
				});
			}
		}
		setAttachments(attachmentList);
		return SUCCESS;
	}
	
	public String deleteAttachment() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.module);
 		
		Chain deleteAttachmentChain = FacilioChainFactory.getDeleteAttachmentChain();
		deleteAttachmentChain.execute(context);
		return SUCCESS;
	}
	
	private FileInfo fileInfo;
	private InputStream downloadStream;
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public InputStream getDownloadStream() {
		return downloadStream;
	}

	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}
	
	public String viewAttachment() {
		
		try {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			
			fileInfo = fs.getFileInfo(this.id);
			if (fileInfo != null) {
				downloadStream = fs.readFile(this.id);
			}
			else {
				return ERROR;
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		
		return SUCCESS;
	}
	
	
	/******************      V2 Api    ******************/
	
	public String v2attachmentList() throws Exception {
		attachmentList();
		setResult(FacilioConstants.ContextNames.ATTACHMENT_LIST, getAttachments());
		return SUCCESS;
	}
	
	public String v2addAttachment() throws Exception {
		addAttachment();
		setResult(FacilioConstants.ContextNames.ATTACHMENT, attachments);
		return SUCCESS;
	}
	
	public String previewAttachment() throws Exception {
		AttachmentContext attachment = AttachmentsAPI.fetchAttachment(module, recordId, fileId);
		if (attachment != null) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			downloadStream = fs.readFile(attachment.getFileId());
			setContentType(attachment.getContentType());
			
			return SUCCESS;
		}
		throw new IllegalArgumentException("Cannot fetch file");
	}
	
	private Long fileId;
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	
}