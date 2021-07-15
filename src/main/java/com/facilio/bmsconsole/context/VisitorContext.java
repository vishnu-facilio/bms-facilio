package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class VisitorContext extends ModuleBaseWithCustomFields{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private VisitorTypeContext visitorType;
	
	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	
	private String name;
	private String email;
	private String phone;
	
	private long lastVisitDuration;
	
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	private LocationContext location;

	public LocationContext getLocation() {
		return location;
	}

	public void setLocation(LocationContext location) {
		this.location = location;
	}

	public long getLocationId() {
		// TODO Auto-generated method stub
		if (location != null) {
			return location.getId();
		}
		return -1;
	}
	

	private long avatarId;
	public long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(long avatarId) {
		this.avatarId = avatarId;
	}

	private String avatarUrl;
	
	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	private File avatar;
	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	private String avatarFileName;
	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	private  String avatarContentType;

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}
	
	private long lastVisitedTime;
	private long firstVisitedTime;
	
	private ContactsContext lastVisitedHost;
	private BaseSpaceContext lastVisitedSpace;

	public long getLastVisitedTime() {
		return lastVisitedTime;
	}

	public void setLastVisitedTime(long lastVisitedTime) {
		this.lastVisitedTime = lastVisitedTime;
	}

	
	public ContactsContext getLastVisitedHost() {
		return lastVisitedHost;
	}

	public void setLastVisitedHost(ContactsContext lastVisitedHost) {
		this.lastVisitedHost = lastVisitedHost;
	}

	private PeopleContext lastVisitedPeople;  
   
	public PeopleContext getLastVisitedPeople() {
		return lastVisitedPeople;
	}

	public void setLastVisitedPeople(PeopleContext lastVisitedPeople) {
		this.lastVisitedPeople = lastVisitedPeople;
	}
	
	public BaseSpaceContext getLastVisitedSpace() {
		return lastVisitedSpace;
	}

	public void setLastVisitedSpace(BaseSpaceContext lastVisitedSpace) {
		this.lastVisitedSpace = lastVisitedSpace;
	}
	
	private long signatureId;
	private String signatureUrl;
	private File signature;
	
	private String signatureFileName;
	private  String signatureContentType;

	public long getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(long signatureId) {
		this.signatureId = signatureId;
	}

	public String getSignatureUrl() {
		return signatureUrl;
	}

	public void setSignatureUrl(String signatureUrl) {
		this.signatureUrl = signatureUrl;
	}

	public File getSignature() {
		return signature;
	}

	public void setSignature(File signature) {
		this.signature = signature;
	}

	public String getSignatureFileName() {
		return signatureFileName;
	}

	public void setSignatureFileName(String signatureFileName) {
		this.signatureFileName = signatureFileName;
	}

	public String getSignatureContentType() {
		return signatureContentType;
	}

	public void setSignatureContentType(String signatureContentType) {
		this.signatureContentType = signatureContentType;
	}
	
	public long getLastVisitDuration() {
		return lastVisitDuration;
	}

	public void setLastVisitDuration(long lastVisitDuration) {
		this.lastVisitDuration = lastVisitDuration;
	}

	public long getFirstVisitedTime() {
		return firstVisitedTime;
	}

	public void setFirstVisitedTime(long firstVisitedTime) {
		this.firstVisitedTime = firstVisitedTime;
	}
	
	private Boolean isReturningVisitor;

	public Boolean getIsReturningVisitor() {
		return isReturningVisitor;
	}

	public void setIsReturningVisitor(Boolean isReturning) {
		this.isReturningVisitor = isReturning;
	}

	public boolean isReturningVisitor() {
		if (isReturningVisitor != null) {
			return isReturningVisitor.booleanValue();
		}
		return false;
	}
	
}
