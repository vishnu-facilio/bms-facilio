package com.facilio.accounts.sso;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;

public class AccountSSO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private String name;
	private SSOType ssoType = SSOType.SAML;
	private SSOConfig config;
	private Boolean isActive;
	private long createdTime;
	private long createdBy;
	private long modifiedTime;
	private long modifiedBy;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	
	public int getSsoType() {
		if (ssoType != null) {
			return ssoType.getIndex();
		}
		return -1;
		
	}
	public void setSsoType(int ssoType) {
		this.ssoType = SSOType.valueOf(ssoType);
	}
	public void setSsoType(SSOType ssoType) {
		this.ssoType = ssoType;
	}
	public SSOType getSsoTypeEnum() {
		return ssoType;
	}

	public SSOConfig getSSOConfig() {
		return this.config;
	}
	
	public JSONObject getConfig() throws Exception {
		if (this.config != null) {
			return FieldUtil.getAsJSON(this.config);
		}
		return null;
	}
	
	public void setConfig(JSONObject configJson) throws Exception {
		this.config = FieldUtil.getAsBeanFromJson(configJson, SamlSSOConfig.class);
	}
	
	public String getConfigJSON() throws Exception {
		if (this.config != null) {
			return FieldUtil.getAsJSON(this.config).toJSONString();
		}
		return null;
	}
	
	public void setConfigJSON(String configJsonStr) throws Exception {
		if (configJsonStr != null && !configJsonStr.trim().isEmpty()) {
			JSONObject configJson = (JSONObject) new JSONParser().parse(configJsonStr);
			this.setConfig(configJson);
		}
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public static enum SSOType implements FacilioIntEnum {
		SAML;
		
		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}
		
		public static SSOType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
