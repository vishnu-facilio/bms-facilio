package com.facilio.datamigration.context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DataMigrationMappingContext {

    private static final long serialVersionUID = 1L;

    private long id = -1;
    private long orgId = -1;
    private long moduleId = -1;
    private String moduleName;
    private long oldId = -1;
    private long newId = -1;
    private long migrationId;

}
