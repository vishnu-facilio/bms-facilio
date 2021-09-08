package com.facilio.bmsconsole.imports;

import com.facilio.bmsconsole.imports.config.ImportConfig;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.annotation.Module;
import org.apache.commons.chain.Context;

import java.util.function.Supplier;

public class ImportConfiguration {


    @Module(value = "asset")
    public static Supplier<ImportConfig> getAssetImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder()
                .uploadHandler()
                .beforeUploadCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .insertHandler()
                .beforeInsertCommand(new FacilioCommand() {
                    @Override
                    public boolean executeCommand(Context context) throws Exception {
                        return false;
                    }
                }).done()
                .build();
    }
}
