package com.facilio.accounts.dto;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long groupId = -1;
	private long orgId = -1;
	private String name;
	private String email;
	private String description;
//	private boolean isActive;
	private long createdTime = -1;
	private long createdBy = -1;
	private long parent = -1;
	private long siteId = -1;
	
	public long getGroupId() {
		return groupId;
	}
	public long getId() {
		return groupId;
	}
	public void setId(long id) {
		this.groupId = id;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	public boolean isActive() {
//		return isActive;
//	}
//	public void setActive(boolean isActive) {
//		this.isActive = isActive;
//	}
//	
	private Boolean isActive;
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isActive() {
		if(isActive != null) {
			return isActive.booleanValue();
		}
		return false;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	public long getParent() {
		return parent;
	}
	public void setParent(long parent) {
		this.parent = parent;
	}
	
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	private String groupMembersEmail;
	public String getGroupMembersEmail() {
		return groupMembersEmail;
	}
	public void setGroupMembersEmail(String groupMembersEmail) {
		this.groupMembersEmail = groupMembersEmail;
	}
	
	private String groupMembersPhone;
	public String getGroupMembersPhone() {
		return groupMembersPhone;
	}
	public void setGroupMembersPhone(String groupMembersPhone) {
		this.groupMembersPhone = groupMembersPhone;
	}
	
	private List<GroupMember> members;
	public List<GroupMember> getMembers() {
		return members;
	}
	public void setMembers(List<GroupMember> members) {
		this.members = members;
	}
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	} 
}
