package com.facilio.multiImport.context;

public class ImportFileSheetsContext {

    private long id = -1;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long importFileId;

    public void setImportFileId(long importFileId) {
        this.importFileId = importFileId;
    }

    public long getImportFileId() {
        return importFileId;
    }

    private long rowCount = -1;

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public long getRowCount() {
        return rowCount;
    }

    private long moduleId = -1;

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public long getModuleId() {
        return moduleId;
    }

}
