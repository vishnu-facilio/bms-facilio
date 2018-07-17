package com.facilio.accounts.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.license.LicenseContext.FacilioLicense;

public class User {
	
	private long uid;
	private String name;
	private String cognitoId;
	private String email;
	private long photoId;
	private String timezone;
	private String password;
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
	private String serverName;
	private FacilioLicense license;
	private Long shiftId;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public FacilioLicense getLicenseEnum() {
		return license;
	}
	public void setLicense(FacilioLicense license) {
		this.license = license;
	}
	public int getLicense() {
		if (license != null) {
			return license.getValue();
		}
		return 0;
	}
	public void setLicense(int licenseValue) {
		this.license = FacilioLicense.valueOf(licenseValue);
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
/*	public String getCognitoId() {
		return cognitoId;
	}
	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}*/
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	
	private String avatarUrl;
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
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
	
	private Role role;
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
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

	public boolean isPortalUser()
	{
		if(portalId>0)
		{
			return true;
		}
		return false;
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
		if(moduleName.equals("workorder") || moduleName.equals("workorderrequest") || moduleName.equals("planned") || moduleName.equals("alarm"))
		{
			Condition condition = new Condition();
			if (moduleName.equals("planned")) {
				condition.setColumnName("Workorder_Template.RESOURCE_ID");
			}
			else {
				condition.setColumnName("RESOURCE_ID");
			}
			condition.setFieldName("resourceId");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		if(moduleName.equals("asset")) {
			Condition condition = new Condition();
			condition.setColumnName("SPACE_ID");
			condition.setFieldName("spaceId");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
			if (userType == 0) {
				criteria.addAndCondition(CriteriaAPI.getCondition("hideToCustomer", "HIDE_TO_CUSTOMER", String.valueOf(false), BooleanOperators.IS));
			}
		}
		if(moduleName.equals("site")) {
			Condition condition = new Condition();
			condition.setColumnName("Resources.ID");
			condition.setFieldName("id");
			condition.setOperator(PickListOperators.IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		if(moduleName.equals("building") || moduleName.equals("floor") || moduleName.equals("space") || moduleName.equals("zone")) {
			Condition condition = new Condition();
			condition.setColumnName("Resources.ID");
			condition.setFieldName("id");
			condition.setOperator(BuildingOperator.BUILDING_IS);
			condition.setValue(StringUtils.join(accessibleSpace, ","));

			criteria = new Criteria();
			criteria.addAndCondition(condition);
		}
		return criteria;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Long getShiftId() {
		return shiftId;
	}
	public void setShiftId(Long shiftId) {
		this.shiftId = shiftId;
	}
}