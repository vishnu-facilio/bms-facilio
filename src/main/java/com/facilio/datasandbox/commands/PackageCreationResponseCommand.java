package com.facilio.datasandbox.commands;

import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.SandboxDataMigrationUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Log4j
public class PackageCreationResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext = (Map<String, Stack<ModuleCSVFileContext>>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        DataMigrationStatusContext dataMigrationObj = DataMigrationConstants.getDataMigrationStatusContext(context);

        SandboxDataMigrationUtil.constructResponse(dataMigrationObj, migrationModuleNameVsDetails, moduleNameVsCsvFileContext);

        return false;
    }
}
