package com.facilio.accounts.dto;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.io.Serializable;
import java.util.List;
@Getter@Setter@Deprecated
public class Group extends ModuleBaseWithCustomFields implements Serializable {

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

	public long getGroupId() {
		return getId();
	}
	public void setGroupId (long id) {
		setId(id);
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
