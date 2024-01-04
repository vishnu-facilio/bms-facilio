package com.facilio.datamigration.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class DataMigrationStatusContext {

    private static final long serialVersionUID = 1L;

    private long id = -1;
    private long orgId = -1;
    private long sourceOrgId = -1;
    private long lastModuleId = -1;
    private String lastModuleName;
    private String moduleFileName;
    private String packageFilePath;
    private long migratedCount = -1;
    private long sysCreatedTime = -1;
    private long sysCreatedBy = -1;
    private long sysModifiedTime = -1;
    private DataMigrationStatus status;

    public DataMigrationStatus getStatusEnum() {
        return status;
    }
    public void setStatus(DataMigrationStatus status) {
        this.status = status;
    }
    public int getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return -1;
    }
    public void setStatus(int status) {
        this.status = DataMigrationStatus.valueOf(status);
    }

    public static enum DataMigrationStatus implements FacilioIntEnum {
        // Package Creation
        CREATION_INITIATED("creationInitiated"),
        MODULE_DATA_CSV_CREATION("moduleDataCSVCreation"),
        DEPENDENT_MODULE_DATA_CSV_CREATION("dependentModuleDataCSVCreation"),
        SPECIAL_MODULE_DATA_CSV_CREATION("specialModuleDataCSVCreation"),
        CREATION_COMPLETED("creationCompleted"),
        // Package Installation
        INSTALLATION_INITIATED("installationInitiated"),
        CUSTOMIZATION_MAPPING("customizationMapping"),
        MODULE_DATA_INSERTION("moduleDataInsertion"),
        READING_MODULE_DATA_INSERTION("readingModuleInsertion"),
        SPECIAL_MODULE_DATA_INSERTION("specialModuleInsertion"),
        MODULE_DATA_UPDATION("moduleDataUpdation"),
        META_MODULES_REUPDATE("metaModuleReUpdate"),
        INSTALLATION_COMPLETED("installationCompleted");
        private String name;

        DataMigrationStatus(String name) {
            this.name = name;
        }

        public static DataMigrationStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
