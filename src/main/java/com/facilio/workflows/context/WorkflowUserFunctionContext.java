package com.facilio.workflows.context;

public class WorkflowUserFunctionContext extends WorkflowContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Long nameSpaceId;
	String name;
	String linkName;
	String nameSpaceName;
	private long sourceBundle;
	
	public Long getNameSpaceId() {
		return nameSpaceId;
	}
	public void setNameSpaceId(Long nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameSpaceName() {
		return nameSpaceName;
	}
	public void setNameSpaceName(String nameSpaceName) {
		this.nameSpaceName = nameSpaceName;
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
	
}
