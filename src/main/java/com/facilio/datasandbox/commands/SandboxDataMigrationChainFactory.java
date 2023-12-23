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
        chain.addCommand(new AddSpecialModuleCSVFilesCommand());
        chain.addCommand(new PackageCreationResponseCommand());
        return chain;
    }

    public static FacilioChain getInstallDataMigrationChain(int transactionTimeout){
        FacilioChain c = getDefaultChain(transactionTimeout);
        c.addCommand(new ValidateDataMigrationInstallationCommand());
        c.addCommand(new AddNecessaryParamsForInstallationToContext());
        c.addCommand(new ComputeModuleSequenceForPackageInstallation());
        c.addCommand(new HandlePickListTypeModulesCommand());
        c.addCommand(new SandboxDataInsertCommand());
        c.addCommand(new ReadingDataInsertCommand());
        c.addCommand(new AddSpecialModuleDataCommand());
        c.addCommand(new SandboxDataUpdateCommand());
        c.addCommand(new ReUpdateMetaModulesCommand());
        return c;
    }
}
