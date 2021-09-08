package com.facilio.bmsconsole.imports.config;

import com.facilio.command.FacilioCommand;
import com.facilio.v3.annotation.Module;
import org.apache.commons.chain.Context;

import java.util.function.Supplier;

public class ImportConfig {
    private UploadHandler uploadHandler;
    private InsertHandler insertHandler;

    private ImportConfig() {}

    private ImportConfig(ImportConfigBuilder builder) {
        this.uploadHandler = builder.uploadHandler;
        this.insertHandler = builder.insertHandler;
    }

    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    public InsertHandler getInsertHandler() {
        return insertHandler;
    }

    public static class ImportConfigBuilder {
        UploadHandler uploadHandler;
        InsertHandler insertHandler;

        public UploadHandler.UploadHandlerBuilder uploadHandler() {
            return new UploadHandler.UploadHandlerBuilder(this);
        }

        public InsertHandler.InsertHandlerBuilder insertHandler() {
            return new InsertHandler.InsertHandlerBuilder(this);
        }

        public ImportConfig build() {
            return new ImportConfig(this);
        }
    }
}
