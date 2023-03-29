package com.facilio.multiImport.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.struts2.json.annotations.JSON;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class ImportFileContext implements Serializable {
    private long id = -1;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    private long importId = -1;

    public long getImportId() {
        return importId;
    }

    public void setImportId(long importId) {
        this.importId = importId;
    }

    private long fileId = -1;

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getFileId() {
        return fileId;
    }

    private String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    List<ImportFileSheetsContext> importFileSheetsContext;

    public List<ImportFileSheetsContext> getImportFileSheetsContext() {
        return importFileSheetsContext;
    }

    public void setImportFileSheetsContext(List<ImportFileSheetsContext> importFileSheetsContext) {
        this.importFileSheetsContext = importFileSheetsContext;
    }
    @JsonIgnore
    File file;

    @JSON(deserialize = false)
    public File getFile() {
        return file;
    }
    @JSON(deserialize = false)
    public void setFile(File file) {
        this.file = file;
    }
    private boolean hasSheetMappingDependencies;

    public boolean isHasSheetMappingDependencies() {
        return hasSheetMappingDependencies;
    }

    public void setHasSheetMappingDependencies(boolean hasSheetMappingDependencies) {
        this.hasSheetMappingDependencies = hasSheetMappingDependencies;
    }

    private Long fileSize;
    private Long totalRecords;

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
