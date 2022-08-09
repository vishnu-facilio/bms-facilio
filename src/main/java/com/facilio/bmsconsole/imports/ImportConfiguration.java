package com.facilio.bmsconsole.imports;

import com.facilio.bmsconsole.imports.annotations.ImportModule;
import com.facilio.bmsconsole.imports.config.ImportConfig;
import com.facilio.bmsconsoleV3.commands.plannedmaintenance.WhitelistRequiredFieldsCommand;
import com.facilio.bmsconsoleV3.commands.pmImport.HandleResourcePlannerImportCommand;
import com.facilio.bmsconsoleV3.commands.pmImport.HandleTasksImportCommand;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import java.util.function.Supplier;

public class ImportConfiguration {

    @ImportModule(value = "test_module")
    public static Supplier<ImportConfig> getAssetImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .beforeUploadCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .parseHandler()
                .uniqueFunction((rowNumber, rowValue, context) -> {
                    return null;
                }).done()
                .importHandler()
                .beforeImportCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .build();
    }

    @ImportModule(value = "plannedmaintenance")
    public static Supplier<ImportConfig> getPlannedMaintenanceImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .beforeParseCommand(new WhitelistRequiredFieldsCommand())
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "pmPlanner")
    public static Supplier<ImportConfig> getPmPlannerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "pmTrigger")
    public static Supplier<ImportConfig> getPmTriggerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "pmResourcePlanner")
    public static Supplier<ImportConfig> getPmResourcePlannerImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .done()
                .build();
    }

    @ImportModule(value = "resourceplanner")
    public static Supplier<ImportConfig> getPmImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()

                .importHandler()
                .lookupMainFieldMap("plannedmaintenance", "name")
                .lookupMainFieldMap("users", "email")
                .afterImportCommand(new HandleResourcePlannerImportCommand())
                .done()
                .build();
    }

    @ImportModule(value = "pmTasksImport")
    public static Supplier<ImportConfig> getPmTasksImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .done()

                .parseHandler()
                .done()
                .importHandler()
                .lookupMainFieldMap("plannedmaintenance", "name")
                .afterImportCommand(new HandleTasksImportCommand())
                .done()
                .build();
    }
}
