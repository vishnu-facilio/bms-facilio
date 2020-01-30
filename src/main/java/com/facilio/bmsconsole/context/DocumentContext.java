package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DocumentContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;
	
	private int documentType;
	public int getDocumentType() {
		return documentType;
	}

	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}
	
	private String documentTypeEnum;
	
	public String getDocumentTypeEnum() {
		return documentTypeEnum;
	}

	public void setDocumentTypeEnum(String documentTypeEnum) {
		this.documentTypeEnum = documentTypeEnum;
	}

	private String documentName;
	private File document;
	private String documentUrl;
	private long documentId;
	private String documentFileName;
	private  String documentContentType;
		
	
	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setDocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	public String getDocumentContentType() {
		return documentContentType;
	}

	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}

	private long parentId;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
}
