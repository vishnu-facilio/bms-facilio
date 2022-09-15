package com.facilio.multiImport.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImportDataDetails {

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

    Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public enum ImportDataStatus {

        DRAFT;

        public int getValue() {
            return ordinal()+1;
        }

        public ImportDataStatus getImportDataStatus(int value) {
            return IMPORT_DATA_STATUS_MAP.get(value);
        }

        private static final Map<Integer, ImportDataStatus> IMPORT_DATA_STATUS_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, ImportDataStatus> initTypeMap() {
            Map<Integer, ImportDataStatus> typeMap = new HashMap<>();
            for(ImportDataStatus type : ImportDataStatus.values()) {
                typeMap.put(type.getValue(), type);
            }
            return typeMap;
        }
    }
}
