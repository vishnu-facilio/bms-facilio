package com.facilio.accounts.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class User {
	
	private long uid;
	private String name;
	private String cognitoId;
	private String email;
	private long photoId;
	private String timezone;
	private String language;
	private String phone;
	private String mobile;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private long ouid;
	private long orgId;
	private long invitedTime;
	private long roleId;
	private int userType;
	private boolean facilioAuth;
	private long portalId;

	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getName() {
		if (this.name == null && this.email != null && !this.email.isEmpty()) {
			return this.email.substring(0, this.email.indexOf("@"));
		}
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCognitoId() {
		return cognitoId;
	}
	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}
	private Boolean userVerified;
	public Boolean getUserVerified() {
		return userVerified;
	}
	public void setUserVerified(Boolean userVerified) {
		this.userVerified = userVerified;
	}
	public boolean isUserVerified(){
		if(userVerified!=null){
			return userVerified.booleanValue();
		}
		return false;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getPhotoId() {
		return photoId;
	}
	public String getAvatarUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId);
		}
		return null;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public long getOuid() {
		return ouid;
	}
	public long getId() {
		return ouid;
	}
	public void setId(long id) {
		this.ouid = id;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getInvitedTime() {
		return invitedTime;
	}
	public void setInvitedTime(long invitedTime) {
		this.invitedTime = invitedTime;
	}
	
	private Boolean isDefaultOrg;
	public Boolean getIsDefaultOrg() {
		return isDefaultOrg;
	}
	public void setDefaultOrg(Boolean isDefaultOrg) {
		this.isDefaultOrg = isDefaultOrg;
	}
	public boolean isDefaultOrg() {
		if(isDefaultOrg != null) {
			return isDefaultOrg.booleanValue();
		}
		return false;
	}
	
	private Boolean userStatus;
	public Boolean getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Boolean userStatus) {
		this.userStatus = userStatus;
	}
	public boolean isActive() {
		if(userStatus != null) {
			return userStatus.booleanValue();
		}
		return false;
	}
	private Boolean inviteAcceptStatus;
	public Boolean getInviteAcceptStatus() {
		return inviteAcceptStatus;
	}
	public void setInviteAcceptStatus(Boolean inviteAcceptStatus) {
		this.inviteAcceptStatus = inviteAcceptStatus;
	}
	public boolean isInviteAcceptStatus(){
		if(inviteAcceptStatus!=null){
			return inviteAcceptStatus.booleanValue();
		}
		return false;
	}
	public long getRoleId() {
		return roleId;
	}
	public Role getRole() throws Exception {
		return AccountUtil.getRoleBean().getRole(roleId);
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}

	public boolean isFacilioAuth() {
		return facilioAuth;
	}

	public void setFacilioAuth(boolean facilioAuth) {
		this.facilioAuth = facilioAuth;
	}

	public long getPortalId() {
		return portalId;
	}

	public void setPortalId(long portalId) {
		this.portalId = portalId;
	}


	private List<Long> accessibleSpace;
	public List<Long> getAccessibleSpace() {
		return accessibleSpace;
	}
	public void setAccessibleSpace(List<Long> accessibleSpace) {
		this.accessibleSpace = accessibleSpace;
	}

	public Criteria scopeCriteria(String moduleName)
	{
		Criteria criteria = null;
		if(getAccessibleSpace() == null) {
			return null;
		}
		if(moduleName.equals("workorder"))
		{
			Condition condition = new Condition();
			condition.setColumnName("RESOURCE_ID");
			condition.setFieldName("resourceId");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		return criteria;
	}
}