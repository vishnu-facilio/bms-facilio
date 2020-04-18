package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ConnectedAppSAMLContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long connectedAppId;
	private String spEntityId;
	private String spAcsUrl;
	private String spLogoutUrl;

	public long getConnectedAppId() {
		return connectedAppId;
	}

	public void setConnectedAppId(long connectedAppId) {
		this.connectedAppId = connectedAppId;
	}

	public String getSpEntityId() {
		return spEntityId;
	}

	public void setSpEntityId(String spEntityId) {
		this.spEntityId = spEntityId;
	}

	public String getSpAcsUrl() {
		return spAcsUrl;
	}

	public void setSpAcsUrl(String spAcsUrl) {
		this.spAcsUrl = spAcsUrl;
	}

	public String getSpLogoutUrl() {
		return spLogoutUrl;
	}

	public void setSpLogoutUrl(String spLogoutUrl) {
		this.spLogoutUrl = spLogoutUrl;
	}
	
	private SubjectType subjectTypeEnum = SubjectType.USERID_EMAIL;

	public void setSubjectTypeEnum(SubjectType subjectTypeEnum) {
		this.subjectTypeEnum = subjectTypeEnum;
	}
	
	public SubjectType getSubjectTypeEnum() {
		if(subjectType > 0){
			return SubjectType.valueOf(subjectType);
		}
		return subjectTypeEnum;
	}
	
	private int subjectType;

	public int getSubjectType() {
		if(subjectTypeEnum!=null){
			return subjectTypeEnum.getValue();
		}
		return subjectType;
	}

	public void setSubjectType(int entityType) {
		this.subjectType = entityType;
	}

	public enum SubjectType {
		USERID_EMAIL;

		public int getValue() {
			return ordinal() + 1;
		}

		public static SubjectType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private NameIdFormat nameIdFormatEnum = NameIdFormat.EMAIL_ADDRESS;

	public void setNameIdFormatEnum(NameIdFormat nameIdFormatEnum) {
		this.nameIdFormatEnum = nameIdFormatEnum;
	}
	
	public NameIdFormat getNameIdFormatEnum() {
		if(nameIdFormat > 0){
			return NameIdFormat.valueOf(nameIdFormat);
		}
		return nameIdFormatEnum;
	}
	
	private int nameIdFormat;

	public int getNameIdFormat() {
		if(nameIdFormatEnum!=null){
			return nameIdFormatEnum.getValue();
		}
		return nameIdFormat;
	}

	public void setNameIdFormat(int nameIdFormat) {
		this.nameIdFormat = nameIdFormat;
	}

	public enum NameIdFormat {
		UNSPECIFIED,
		EMAIL_ADDRESS;

		public int getValue() {
			return ordinal() + 1;
		}

		public static NameIdFormat valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}


}