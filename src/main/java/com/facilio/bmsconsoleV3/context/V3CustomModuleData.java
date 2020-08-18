package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

import java.io.File;

public class V3CustomModuleData extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Long photoId;
    public Long getPhotoId() {
        return photoId;
    }
    public void setPhotoId(Long photoId) {
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

}
