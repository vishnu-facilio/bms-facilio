package com.facilio.bmsconsole.context;

import java.io.File;
import java.io.Serializable;

import com.facilio.modules.FacilioIntEnum;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.workflow.rule.FormInterface;
import com.facilio.modules.FacilioModule;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServiceCatalogContext implements Serializable, FormInterface {
	
	
	private long appId = -1;

    private long id = -1;
    public long getId() {
        return id;
    }
    public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
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

    private long groupId = -1;
    public long getGroupId() {
        return groupId;
    }
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    private ServiceCatalogGroupContext group;
    public ServiceCatalogGroupContext getGroup() {
        return group;
    }
    public void setGroup(ServiceCatalogGroupContext group) {
        this.group = group;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public void setType(Type type) {
        this.type = type;
    }

    private long moduleId;
    public long getModuleId() {
        return moduleId;
    }
    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }
    
    private Boolean complaintType;
    @JSON(serialize = false)
    @JsonIgnore
    public Boolean getComplaintType() {
		return complaintType;
	}
    @JsonIgnore
	public void setComplaintType(Boolean complaintType) {
		this.complaintType = complaintType;
	}
	@JSON(serialize = false)
	public boolean isComplaintType() {
		if (complaintType == null) {
			return false;
		}
		return true;
	}

	private FacilioModule module;
    public FacilioModule getModule() {
        return module;
    }
    public void setModule(FacilioModule module) {
        this.module = module;
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

    private String externalURL;
    public String getExternalURL() {
        return externalURL;
    }
    public void setExternalURL(String externalURL) {
        this.externalURL = externalURL;
    }

    public enum Type implements FacilioIntEnum {
	    MODULE_FORM("Module Form"),
        EXTERNAL_LINK("External Link");

	    private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
