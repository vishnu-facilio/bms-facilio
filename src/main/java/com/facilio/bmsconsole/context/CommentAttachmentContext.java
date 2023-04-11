package com.facilio.bmsconsole.context;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class CommentAttachmentContext extends ModuleBaseWithCustomFields {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long commentModuleId = -1;
    public long getCommentModuleId() {
        return commentModuleId;
    }
    public void setCommentModuleId(long commentModuleId) {
        this.commentModuleId = commentModuleId;
    }

    private long fileId = -1;
    public long getFileId() {
        return fileId;
    }
    public void setFileId(long fileId) throws Exception {
        this.fileId = fileId;
        getPreviewUrl();
        getDownloadUrl();
        getOriginalUrl();
    }

    private long createdTime = -1;
    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    private String fileName;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private long fileSize = -1;
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    private long uploadedBy = -1;
    public long getUploadedBy() {
        return uploadedBy;
    }
    public void setUploadedBy(long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    private long uploadedTime = -1;
    public long getUploadedTime() {
        return uploadedTime;
    }
    public void setUploadedTime(long uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    private String contentType;
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private AttachmentType type;
    public int getType() {
        if(type != null) {
            return type.getIntVal();
        }
        return -1;
    }
    public void setType(int type) {
        if (type != -1) {
            this.type = AttachmentType.getType(type);
        }
    }
    public void setType(AttachmentType type) {
        this.type = type;
    }

    private String previewUrl;
    public String getPreviewUrl() throws Exception {
        if (this.previewUrl == null && this.fileId > 0) {
            if (!AccountUtil.getCurrentAccount().isFromMobile() && StringUtils.isNotEmpty(attachmentModule) && recordId > 0) {
                StringBuffer url = ServletActionContext.getRequest().getRequestURL();
                StringBuilder builder = new StringBuilder(url.substring(0, url.indexOf("/api")))
                        .append("/api/v2/").append(attachmentModule).append("/attachment/").append(recordId).append("?fileId=").append(fileId);
                return builder.toString();
            }
            FileStore fs = FacilioFactory.getFileStore();
            previewUrl = fs.getPrivateUrl(this.fileId);
        }
        return previewUrl;
    }

    private String downloadUrl;
    public String getDownloadUrl() throws Exception {
        if (this.downloadUrl == null && this.fileId > 0) {
            FileStore fs = FacilioFactory.getFileStore();
            downloadUrl = fs.getDownloadUrl(this.fileId);
        }
        return downloadUrl;
    }
    public String getOriginalUrl() throws Exception {
        if (this.originalUrl == null && this.fileId > 0) {
            FileStore fs = FacilioFactory.getFileStore();
            originalUrl = fs.orginalFileUrl(this.fileId);
        }
        return originalUrl;
    }
    private String originalUrl;
    private String attachmentModule;
    public void setAttachmentModule(String attachmentModule) {
        this.attachmentModule = attachmentModule;
    }

    private long recordId = -1;
    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public static enum AttachmentType {
        BEFORE,
        AFTER
        ;

        public int getIntVal() {
            return ordinal()+1;
        }

        public static AttachmentType getType(int type) {
            return ATTACHMENT_TYPES[type -1];
        }

        private static final AttachmentType[] ATTACHMENT_TYPES = AttachmentType.values();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return new StringBuilder("Attachment [")
                .append("parentId : ").append(commentModuleId).append(", ")
                .append("fileName : ").append(fileName)
                .append("]")
                .toString();
    }
    private long parent;

    public long getParent() {
        return parent;
    }
    public void setParent(long parent) {
        this.parent = parent;
    }
}
