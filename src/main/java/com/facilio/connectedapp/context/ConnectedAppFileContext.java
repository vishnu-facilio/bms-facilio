package com.facilio.connectedapp.context;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ConnectedAppFileContext implements Serializable {
    private long id;
    private long orgId;
    private long connectedAppId;
    private String fileName;
    private long fileSize;
    private long fileId;
    private File content;
    private String contentFileName;
    private String contentContentType;
    private String rawContent;
    private String filePath = "";
    private String contentType;
    private boolean isDirectory;
    private long parentId;
    private long sysCreatedTime;
    private long sysCreatedBy;
    private IAMUser sysCreatedByUser;
    private long sysModifiedTime;
    private long sysModifiedBy;
    private IAMUser sysModifiedByUser;
    private long sysDeletedTime;
    private long sysDeletedBy;
    private List<ConnectedAppFileContext> children;

    public boolean isEditable() {
        if (getFileName() != null) {
            String extn = FilenameUtils.getExtension(getFileName());
            extn = extn == null ? "" : extn;
            return ConnectedAppHostingAPI.EDIT_SUPPORTED_FILE_TYPES.contains(extn.trim().toLowerCase());
        }
        return false;
    }
    public boolean isRootFolder() {
        if (getParentId() <= 0) {
            return true;
        }
        return false;
    }
}
