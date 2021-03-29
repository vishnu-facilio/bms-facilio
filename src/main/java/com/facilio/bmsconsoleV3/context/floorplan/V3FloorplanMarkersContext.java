

package com.facilio.bmsconsoleV3.context.floorplan;
import com.facilio.v3.context.V3Context;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.fasterxml.jackson.annotation.JsonIgnore;
public class V3FloorplanMarkersContext  extends V3Context {


	private String name;
	private String description;
	private Long fileId;
	private String avatarUrl;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}


	@JsonIgnore
	public String getAvatarUrl() throws Exception {	
		if (avatarUrl == null && this.fileId > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			avatarUrl = fs.getPrivateUrl(this.fileId);
		}
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


}