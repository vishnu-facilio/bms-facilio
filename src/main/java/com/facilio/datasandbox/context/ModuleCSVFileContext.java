package com.facilio.datasandbox.context;

import com.facilio.datamigration.util.DataMigrationConstants;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ModuleCSVFileContext {
    private int order;
    private int recordCount;
    private String moduleName;
    private String csvFileName;

    public boolean isReachedThreshold() {
        return recordCount == DataMigrationConstants.MAX_RECORDS_PER_FILE;
    }

    public ModuleCSVFileContext(String moduleName, String csvFileName, int order) {
        this(moduleName, csvFileName, order, 0);
    }

    public ModuleCSVFileContext(String moduleName, String csvFileName, int order, int recordCount) {
        this.order = order;
        this.moduleName = moduleName;
        this.csvFileName = csvFileName;
        this.recordCount = recordCount;
    }
}
