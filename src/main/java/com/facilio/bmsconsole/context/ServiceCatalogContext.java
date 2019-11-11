package com.facilio.bmsconsole.context;

import java.io.File;
import java.io.Serializable;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.FormInterface;

public class ServiceCatalogContext implements Serializable, FormInterface {

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    private long photoId = -1;
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

	private  String photoContentType;
	public String getPhotoContentType() {
		return photoContentType;
	}
	public void setPhotoContentType(String photoContentType) {
		this.photoContentType = photoContentType;
	}

    private long moduleId;
    public long getModuleId() {
        return moduleId;
    }
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    private long formId = -1;
    public long getFormId() {
        return formId;
    }
    public void setFormId(long formId) {
        this.formId = formId;
    }

    private FacilioForm form;
    public FacilioForm getForm() {
        return form;
    }
    public void setForm(FacilioForm form) {
        this.form = form;
    }

    private long groupId = -1;
    public long getGroupId() {
        return groupId;
    }
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
