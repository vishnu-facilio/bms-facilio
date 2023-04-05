package com.facilio.multiImport;

import com.facilio.bmsconsoleV3.context.V3CustomModuleData;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.multiImport.annotations.ImportModule;
import com.facilio.multiImport.config.ImportConfig;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.function.Supplier;

public class MultiImportConfigurations {
    @ImportModule("workorder")
    public static Supplier<ImportConfig> getWorkOrderImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder(V3WorkOrderContext.class)
                .uploadHandler()
                .beforeUploadCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .parseHandler()
                .uniqueFunction((rowNumber,rowValue,prop, context) -> {
                    return null;
                }).done()
                .importHandler()
                .lookupUniqueFieldsMap("ticketstatus",Arrays.asList("displayname"))
                .beforeImportCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .build();
    }
}
