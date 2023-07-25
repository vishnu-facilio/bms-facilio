package com.facilio.componentpackage.command;

import com.facilio.chain.FacilioChain;

public class PackageChainFactory {

	private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain(600000);
    }

    public static FacilioChain getCreatePackageChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ValidateAndCreatePackageCommand());
        c.addCommand(new PopulateExistingChangesetMappingCommand());
        c.addCommand(new CreateXMLPackageCommand());

        return c;
    }

    public static FacilioChain getDeployPackageChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ValidatePackageCreationPermission());
        c.addCommand(new UnzipPackageFileCommand());
        c.addCommand(new DeployPackageCommand());
        c.addCommand(new DeployPackageComponentCommand());
        c.addCommand(new ComponentPostTransactionCommand());

        return c;
    }

    public static FacilioChain getCreatePackageForSandboxChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new GetSandboxPackageUniqueNameCommand());
        c.addCommand(new ValidateAndCreatePackageCommand());
        c.addCommand(new PopulateExistingChangesetMappingCommand());
        c.addCommand(new CreateXMLPackageCommand());
        c.addCommand(new DeployPackageCommand());
        c.addCommand(new DeployPackageComponentCommand());
        c.addCommand(new ComponentPostTransactionCommand());

        return c;
    }
    
}
