package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;

public class SingleSharingContext implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	
	private long fieldId = -1;

	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	private long companyId = -1;
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long userId = -1;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	private long roleId = -1;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	
	private long groupId = -1;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	private List<GroupMember> groupMembers;
	public List<GroupMember> getGroupMembers() {
		return groupMembers;
	}
	public void setGroupMembers(List<GroupMember> groupMembers) {
		this.groupMembers = groupMembers;
	}

	private SharingType type;
	public SharingType getTypeEnum() {
		return type;
	}
	public void setType(SharingType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = SharingType.valueOf(type);
	}

	public static enum SharingType {
		USER,
		ROLE,
		GROUP,
		FIELD,
		APP,
		TENANT,
		VENDOR,
		PEOPLE,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static SharingType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}

	public boolean shouldSkip(Object object) throws Exception {
		switch (getTypeEnum()) {
			case FIELD:
				if (object instanceof ModuleBaseWithCustomFields) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = modBean.getField(getFieldId());

					Object value = FieldUtil.getValue((ModuleBaseWithCustomFields) object, field);
					if (FacilioUtil.isEmptyOrNull(value)) {
						return true;
					}
				}
				break;
		}
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder("Parent ID : ")
									.append(parentId)
									.append("::Type : ")
									.append(type)
									.append("::");
		
		if (type != null) {
			switch (type) {
				case USER:
					builder.append(userId);
					break;
				case ROLE:
					builder.append(roleId);
					break;
				case GROUP:
					builder.append(groupId);
					break;
				case FIELD:
				case TENANT:
				case VENDOR:
					builder.append(fieldId);
					break;
				case APP:
					builder.append(appType);
					break;
				case PEOPLE:
					builder.append(peopleId);
					break;
			}
		}
		
		return builder.toString();
	}

	private int appType;
	public int getAppType() {
		return appType;
	}
	public void setAppType(AppDomain.AppDomainType appType) {
		this.appType = appType.getIndex();
	}
	public void setAppType(int appType) {
		this.appType = appType;
	}

	@Getter
	@Setter
	private long sharedBy = -1L;


	public long getPeopleId() {
		return peopleId;
	}
	public void setPeopleId(long peopleId) {
		this.peopleId = peopleId;
	}
	private long peopleId = -1;
}
