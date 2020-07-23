package com.facilio.v3.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.File;

public class AttachmentV3Context extends V3Context {

    private static final long serialVersionUID = 1L;

    public int getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(int attachmentType) {
        this.attachmentType = attachmentType;
    }

    private int attachmentType;


    public String getAttachmentTypeEnum() {
        return attachmentTypeEnum;
    }

    public void setAttachmentTypeEnum(String attachmentTypeEnum) {
        this.attachmentTypeEnum = attachmentTypeEnum;
    }

    private String attachmentTypeEnum;


    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentDownloadUrl() {
        return attachmentDownloadUrl;
    }

    public void setAttachmentDownloadUrl(String attachmentDownloadUrl) {
        this.attachmentDownloadUrl = attachmentDownloadUrl;
    }

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    private String attachmentName;
    private File attachment;
    private String attachmentUrl;
    private String attachmentDownloadUrl;
    private long attachmentId;
    private String attachmentFileName;
    private  String attachmentContentType;


    private ModuleBaseWithCustomFields parent;

    public ModuleBaseWithCustomFields getParent() {
        return parent;
    }
    public void setParentId(ModuleBaseWithCustomFields parent) {
        this.parent = parent;
    }

}
