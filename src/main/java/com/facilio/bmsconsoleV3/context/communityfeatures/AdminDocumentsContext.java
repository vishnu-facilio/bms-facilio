package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.v3.context.V3Context;

import java.io.File;
import java.util.List;

public class AdminDocumentsContext extends V3Context {

    private Long fileId;
    private String fileUrl;
    private File file;
    private String fileFileName;
    private String fileContentType;
    private String description;
    private String title;
    private List<CommunitySharingInfoContext> admindocumentsharing;


    public List<CommunitySharingInfoContext> getAdmindocumentsharing() {
        return admindocumentsharing;
    }

    public void setAdmindocumentsharing(List<CommunitySharingInfoContext> admindocumentsharing) {
        this.admindocumentsharing = admindocumentsharing;
    }



    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private AudienceContext audience;

    public AudienceContext getAudience() {
        return audience;
    }

    public void setAudience(AudienceContext audience) {
        this.audience = audience;
    }
}
