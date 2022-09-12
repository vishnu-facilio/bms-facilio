package com.facilio.multiImport.context;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.List;

public class ImportFileContext {
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

    File file;

    @JsonIgnore
    public File getFile() {
        return file;
    }

    @JsonIgnore
    public void setFile(File file) {
        this.file = file;
    }
}
