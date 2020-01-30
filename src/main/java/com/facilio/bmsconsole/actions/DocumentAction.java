package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DocumentContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class DocumentAction extends FacilioAction{

private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String parentAttachmentModuleName;
	
	public String getParentAttachmentModuleName() {
		return parentAttachmentModuleName;
	}
	public void setParentAttachmentModuleName(String parentAttachmentModuleName) {
		this.parentAttachmentModuleName = parentAttachmentModuleName;
	}

	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	private DocumentContext document;
	private List<DocumentContext> documents;
	
	private List<Long> documentIds;
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}
	
	private long parentId;
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public DocumentContext getDocument() {
		return document;
	}
	public void setDocument(DocumentContext document) {
		this.document = document;
	}
	public List<DocumentContext> getDocuments() {
		return documents;
	}
	public void setDocuments(List<DocumentContext> documents) {
		this.documents = documents;
	}
	public List<Long> getDocumentIds() {
		return documentIds;
	}
	public void setDocumentIds(List<Long> documentIds) {
		this.documentIds = documentIds;
	}
	public String addDocuments() throws Exception {
		
		if(!CollectionUtils.isEmpty(documents)) {
			FacilioChain c = TransactionChainFactory.addDocumentsChain();
			c.getContext().put(FacilioConstants.ContextNames.MODULE_NAME,getParentAttachmentModuleName());
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, documents);
			c.execute();
			setResult(FacilioConstants.ContextNames.DOCUMENTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateDocuments() throws Exception {
		
		if(!CollectionUtils.isEmpty(documents)) {
			FacilioChain c = TransactionChainFactory.updateDocumentsChain();
			c.getContext().put(FacilioConstants.ContextNames.MODULE_NAME,getParentAttachmentModuleName());
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, documents);
			c.execute();
			setResult(FacilioConstants.ContextNames.DOCUMENTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteDocuments() throws Exception {
		
		if(!CollectionUtils.isEmpty(documentIds)) {
			FacilioChain c = FacilioChainFactory.deleteDocumentsChain();
			c.getContext().put(FacilioConstants.ContextNames.MODULE_NAME,getParentAttachmentModuleName());
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, documentIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getDocumentList() throws Exception {
		FacilioChain c = ReadOnlyChainFactory.getDocumentListChain();
		
		c.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, getParentAttachmentModuleName());
		c.getContext().put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		c.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		
		c.execute();
		if(getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT, c.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			setResult(FacilioConstants.ContextNames.RECORD_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		
		
		return SUCCESS;
	}
	
}
