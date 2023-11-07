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
    private long migratedCount = -1;
    private long sysCreatedTime = -1;
    private long sysCreatedBy = -1;
    private long sysModifiedTime = -1;
    private DataMigrationStatus status;
    private long packageId = -1;

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

        INITIATED("initiated"),
        CUSTOMIZATION_MAPPING_IN_PROGRESS("customizationMapping"),
        CREATION_IN_PROGRESS("creationInProgress"),
        UPDATION_IN_PROGRESS("updateInProgress"),
        COMPLETED("completed");

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
