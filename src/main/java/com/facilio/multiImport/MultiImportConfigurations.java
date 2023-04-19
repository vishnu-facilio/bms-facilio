package com.facilio.multiImport;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.multiImport.annotations.ImportModule;
import com.facilio.multiImport.config.ImportConfig;

import java.util.Arrays;
import java.util.function.Supplier;

public class MultiImportConfigurations {
    @ImportModule("workorder")
    public static Supplier<ImportConfig> getWorkOrderImportConfig() {
        return () -> new ImportConfig.ImportConfigBuilder(V3WorkOrderContext.class)
                .importHandler()
                .loadLookUpExtraSelectFields("workorder", Arrays.asList("status"))
                .done()
                .createHandler()
                .beforeSaveCommand(TransactionChainFactoryV3.getWorkOrderBeforeCreateImportChain())
                .done()
                .updateHandler()
                .beforeUpdateCommand()
                .done()
                .build();
    }
}
