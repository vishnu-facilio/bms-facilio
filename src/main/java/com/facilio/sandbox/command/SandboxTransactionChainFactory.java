package com.facilio.sandbox.command;

import com.facilio.chain.FacilioChain;

public class SandboxTransactionChainFactory {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getCreateSandboxChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateSandboxCommand());
        c.addCommand(new PreFillSandboxConfigCommand());
        c.addCommand(new AddOrUpdateSandboxCommand());
        c.addCommand(new AddOrUpdateSandboxSharingCommand());
        c.addCommand(new AddDefaultSignupDataAndCreationInstallationCommand());
        return c;
    }


    public static FacilioChain getUpdateSandboxChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateSandboxCommand());
        c.addCommand(new PreFillSandboxConfigCommand());
        c.addCommand(new AddOrUpdateSandboxCommand());
        c.addCommand(new AddOrUpdateSandboxSharingCommand());
        return c;
    }

    public static FacilioChain getChangeSandboxStatusChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ChangeSandboxStatusCommand());
        return c;
    }
    public static FacilioChain getSandboxOrgCreationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultSignupDataAndCreationInstallationCommand());
        return c;
    }
    public static FacilioChain getSandboxDataCreationInstallationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateAndInstallPackageCommand());
        return c;
    }
    public static FacilioChain getSandboxDataInstallationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InstallPackageCommand());
        return c;
    }
    public static FacilioChain getSandboxOrgSignupChain(){
        FacilioChain c = getDefaultChain();
        c.addCommand(new CreateOrgForSandboxCommand());
        return c;
    }
}
