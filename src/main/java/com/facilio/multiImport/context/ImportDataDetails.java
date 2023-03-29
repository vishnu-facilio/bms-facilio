package com.facilio.multiImport.context;

import com.facilio.multiImport.enums.ImportDataStatus;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportDataDetails implements Serializable {

    private long id = -1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private long createdTime;

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    private long modifiedTime;

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    private long createdBy;

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    private long modifiedBy;

    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public long getModifiedBy() {
        return modifiedBy;
    }

    ImportDataStatus status;
    private long importStartTime;
    private long importEndTime;

    public long getImportStartTime() {
        return importStartTime;
    }

    public void setImportStartTime(long importStartTime) {
        this.importStartTime = importStartTime;
    }

    public long getImportEndTime() {
        return importEndTime;
    }

    public void setImportEndTime(long importEndTime) {
        this.importEndTime = importEndTime;
    }

    public void setStatus(int status) {
        this.status = ImportDataStatus.valueOf(status);
    }
    public void setStatus(ImportDataStatus status) {
        this.status = status;
    }
    public int getStatus() {
        return status!=null? status.getValue() : -1;
    }
    public ImportDataStatus getStatusEnum(){
        return status;
    }
    Integer importType;

    public Integer getImportType() {
        return importType;
    }

    public void setImportType(Integer importType) {
        this.importType = importType;
    }

    private Long totalRecords;
    private Long processedRecordsCount;

    public Long getProcessedRecordsCount() {
        return processedRecordsCount;
    }

    public void setProcessedRecordsCount(Long processedRecordsCount) {
        this.processedRecordsCount = processedRecordsCount;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    List<ImportFileContext> importFiles;

    public List<ImportFileContext> getImportFiles() {
        return importFiles;
    }

    public void setImportFiles(List<ImportFileContext> importFiles) {
        this.importFiles = importFiles;
    }

    private boolean hasErrorRecords;

    public boolean isHasErrorRecords() {
        return hasErrorRecords;
    }

    public void setHasErrorRecords(boolean hasErrorRecords) {
        this.hasErrorRecords = hasErrorRecords;
    }
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if(StringUtils.isNotEmpty(errorMessage) && errorMessage.length()>255){
            errorMessage = errorMessage.trim().substring(0,255);
        }
        this.errorMessage = errorMessage;
    }

    public enum ImportType {

        EXCEL;

        public int getValue() {
            return ordinal() + 1;
        }

        public static ImportType getImportType(int value) {
            return IMPORT_TYPE_MAP.get(value);
        }

        private static final Map<Integer, ImportType> IMPORT_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

        private static Map<Integer, ImportType> initTypeMap() {
            Map<Integer, ImportType> typeMap = new HashMap<>();
            for (ImportType type : ImportType.values()) {
                typeMap.put(type.getValue(), type);
            }
            return typeMap;
        }
    }
}
