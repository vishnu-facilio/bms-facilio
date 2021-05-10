package com.facilio.v3.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.File;

public class AttachmentV3Context extends V3Context {

    private static final long serialVersionUID = 1L;

    private int fileType;

    private String fileTypeEnum;


//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    private String fileName;


    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    private String fileFileName;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private File file;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    private String fileUrl;

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileTypeEnum() {
        return fileTypeEnum;
    }

    public void setFileTypeEnum(String fileTypeEnum) {
        this.fileTypeEnum = fileTypeEnum;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }



    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    private String fileDownloadUrl;
    private long fileId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private  String fileContentType;


    private ModuleBaseWithCustomFields parent;

    public ModuleBaseWithCustomFields getParent() {
        return parent;
    }
    public void setParent(ModuleBaseWithCustomFields parent) {
        this.parent = parent;
    }

    
    public Long getParentId() {
    	if(parent != null) {
    		return parent.getId();
    	}
    	return null;
    }
}
