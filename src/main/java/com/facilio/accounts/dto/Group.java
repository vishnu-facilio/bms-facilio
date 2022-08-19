package com.facilio.accounts.dto;

import java.io.Serializable;
import java.util.List;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;
@Getter@Setter@Deprecated
public class Group extends V3Context implements Serializable {

	private Long groupId;
	private String name;
	private String email;
	private String description;
	private long createdTime = -1;
	private long createdBy = -1;
	private long parent = -1;
	private String phone;
	private String groupMembersEmail;
	private String groupMembersPhone;
	private String groupMembersIds;
	private List<GroupMember> members;
	private Boolean isActive;

	public void setId(Long id) {
		this.groupId = id;
	}

	public Long getGroupId() {
		return getId();
	}

	public boolean isActive() {
		return isActive != null && isActive.booleanValue();
	}

	@JSON(serialize=false)
	public String getGroupMembersEmail() {
		return groupMembersEmail;
	}

	@JSON(serialize=false)
	public String getGroupMembersPhone() {
		return groupMembersPhone;
	}

	@JSON(serialize=false)
	public String getGroupMembersIds() {
		return groupMembersIds;
	}

}
