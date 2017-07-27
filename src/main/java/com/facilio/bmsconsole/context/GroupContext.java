package com.facilio.bmsconsole.context;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.bmsconsole.util.UserAPI;

public class GroupContext extends ContextBase {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long groupId = 0;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	public long getId() {
		return groupId;
	}
	public void setId(long id) {
		this.groupId = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private boolean isActive;
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getStatusAsString() {
		if (this.isActive) {
			return "Active";
		}
		else {
			return "Inactive";
		}
	}
	
	private long createdTime;
	public long getCreatedTime() {
		return createdTime;
	}
	public String getFormattedCreatedTime() {
		SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
		return sd.format(new Date(createdTime));
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	private long createdBy;
	public long getCreatedBy() {
		return createdBy;
	}
	public UserContext getCreatedByUser() throws Exception {
		return UserAPI.getUser(createdBy);
	}
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	private long parent;
	public long getParent() {
		return parent;
	}
	public void setParent(long parent) {
		this.parent = parent;
	}
	
	private int membersCount;
	public int getMembersCount() {
		return membersCount;
	}
	public void setMembersCount(int membersCount) {
		this.membersCount = membersCount;
	}
}
