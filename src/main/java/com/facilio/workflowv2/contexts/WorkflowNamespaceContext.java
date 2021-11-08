package com.facilio.workflowv2.contexts;

import java.util.List;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.workflows.context.WorkflowUserFunctionContext;

public class WorkflowNamespaceContext {

	long id = -1;
	long orgid = -1;
	String linkName;
	String name;
	List<WorkflowUserFunctionContext> functions;
	
	private long sourceBundle;
	
	private Boolean deleted;
	private long deletedBy;
	private long deletedTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<WorkflowUserFunctionContext> getFunctions() {
		return functions;
	}
	public void setFunctions(List<WorkflowUserFunctionContext> functions) {
		this.functions = functions;
	}
	
	private long sysCreatedTime = -1;
	public long getSysCreatedTime() {
		return sysCreatedTime;
	}
	public void setSysCreatedTime(long sysCreatedTime) {
		this.sysCreatedTime = sysCreatedTime;
	}

	private long sysModifiedTime = -1;
	public long getSysModifiedTime() {
		return sysModifiedTime;
	}
	public void setSysModifiedTime(long sysModifiedTime) {
		this.sysModifiedTime = sysModifiedTime;
	}
	
	private long sysCreatedBy;
	public long getSysCreatedBy() {
		return sysCreatedBy;
	}
	public void setSysCreatedBy(long sysCreatedBy) {
		this.sysCreatedBy = sysCreatedBy;
	}

	private long sysModifiedBy;
	public long getSysModifiedBy() {
		return sysModifiedBy;
	}
	public void setSysModifiedBy(long sysModifiedBy) {
		this.sysModifiedBy = sysModifiedBy;
	}
	
	private IAMUser sysCreatedByObj;
	private IAMUser sysModifiedByObj;

	public IAMUser getSysCreatedByObj() {
		return sysCreatedByObj;
	}
	public void setSysCreatedByObj(IAMUser sysCreatedByObj) {
		this.sysCreatedByObj = sysCreatedByObj;
	}
	public IAMUser getSysModifiedByObj() {
		return sysModifiedByObj;
	}
	public void setSysModifiedByObj(IAMUser sysModifiedByObj) {
		this.sysModifiedByObj = sysModifiedByObj;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public long getSourceBundle() {
		return sourceBundle;
	}
	public void setSourceBundle(long sourceBundle) {
		this.sourceBundle = sourceBundle;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public long getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(long deletedBy) {
		this.deletedBy = deletedBy;
	}
	public long getDeletedTime() {
		return deletedTime;
	}
	public void setDeletedTime(long deletedTime) {
		this.deletedTime = deletedTime;
	}
}
