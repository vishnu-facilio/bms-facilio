package com.facilio.bmsconsole.view;

import java.io.File;

import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class CustomModuleData extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private long photoId;
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	private String photoUrl;
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	private File photo;
	public File getPhoto() {
		return photo;
	}
	public void setPhoto(File photo) {
		this.photo = photo;
	}

	private String photoFileName;

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	private String photoContentType;

	public String getPhotoContentType() {
		return photoContentType;
	}

	public void setPhotoContentType(String photoContentType) {
		this.photoContentType = photoContentType;
	}

	@Getter
	@Setter
	private V3FailureClassContext failureClass;

	@Getter
	@Setter
	@JsonProperty("classification")
	private ClassificationContext classification;

	@Getter
	@Setter
	@JsonProperty("makeRecordOffline")
	private Boolean makeRecordOffline;
}
