package com.facilio.datasandbox.commands;

import com.facilio.chain.FacilioChain;

public class SandboxDataMigrationChainFactory {
    private static FacilioChain getDefaultChain(int transactionTimeout) {
        return FacilioChain.getTransactionChain(transactionTimeout);
    }

    public static FacilioChain createDataPackageChain() {
        FacilioChain chain = getDefaultChain(18000000);
        chain.addCommand(new ValidateDataMigrationCreationCommand());
        chain.addCommand(new AddNecessaryParamsForPackageCreation());
        chain.addCommand(new ComputeModuleSequenceForPackageCreation());
        chain.addCommand(new CreateDataCSVFilesCommand());
        chain.addCommand(new UpdateDataCSVFilesCommand());
        chain.addCommand(new AddSpecialModuleCSVFilesCommand());
        chain.addCommand(new PackageCreationResponseCommand());
        return chain;
    }

    public static FacilioChain getInstallDataMigrationChain(){
        FacilioChain c = getDefaultChain(18000000);
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

    public static FacilioChain imsCreateDataPackageChain() {
        FacilioChain c = getDefaultChain(-1);
        c.addCommand(new IMSCreateDataPackageCommand());
        return c;
    }

    public static FacilioChain imsInstallDataPackageChain() {
        FacilioChain c = getDefaultChain(-1);
        c.addCommand(new IMSInstallDataPackageChain());
        return c;
    }
}
