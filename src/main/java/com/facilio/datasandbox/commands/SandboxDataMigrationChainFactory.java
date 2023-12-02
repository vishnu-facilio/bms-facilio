package com.facilio.datasandbox.commands;

import com.facilio.chain.FacilioChain;

public class SandboxDataMigrationChainFactory {
    private static FacilioChain getDefaultChain(int transactionTimeout) {
        return FacilioChain.getTransactionChain(transactionTimeout);
    }

    public static FacilioChain createDataPackageChain(int transactionTimeout) {
        FacilioChain chain = getDefaultChain(transactionTimeout);
        chain.addCommand(new ValidateDataMigrationCreationCommand());
        chain.addCommand(new ComputeModuleSequenceForPackageCreation());
        chain.addCommand(new CreateDataCSVFilesCommand());
        chain.addCommand(new UpdateDataCSVFilesCommand());
//        chain.addCommand(new CreateDataCSVFilesForSubModulesCommand());
        chain.addCommand(new PackageCreationResponseCommand());
        return chain;
    }

    public static FacilioChain getInstallDataMigrationChain(){
        FacilioChain c = getDefaultChain(-1);
        c.addCommand(new ValidateDataMigrationInstallationCommand());
        c.addCommand(new ComputeModuleSequenceForPackageInstallation());
        c.addCommand(new SandboxDataInsertCommand());
        c.addCommand(new SandboxDataUpdateCommand());
//        c.addCommand(new SandboxDataAddModuleAttachmentsCommand());
//        c.addCommand(new SandboxDataAddModuleNotesCommand());

        return c;
    }
}
