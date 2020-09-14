package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLResponseContext extends ModuleBaseWithCustomFields {
	
	
	private static final long serialVersionUID = 1L;
	
	private Long usecaseId;
	private Boolean status;
	private String message;
	private Long orgid;
	private Long assetid;
	private List<Object> resultData;
	private List<MLCustomModuleContext> moduleInfo;
	
	public Long getUsecaseId() {
		return usecaseId;
	}
	
	public void setUsecaseId(Long usecaseId) {
		this.usecaseId = usecaseId;
	}
	
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public List<MLCustomModuleContext> getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(List<MLCustomModuleContext> moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public Long getAssetid() {
		return assetid;
	}

	public void setAssetid(Long assetid) {
		this.assetid = assetid;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public List<Object> getResultData() {
		return resultData;
	}

	public void setResultData(List<Object> resultData) {
		this.resultData = resultData;
	}
	
	@Override
	public String toString() {
		return "MLResponseContext [usecaseId=" + usecaseId + ", status=" + status + ", message=" + message + ", orgid="
				+ orgid + ", assetid=" + assetid + ", resultData=" + resultData + ", moduleInfo=" + moduleInfo + "]";
	}

}
