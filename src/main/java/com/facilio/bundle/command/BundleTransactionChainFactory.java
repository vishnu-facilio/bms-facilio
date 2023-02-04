package com.facilio.bundle.command;

import com.facilio.chain.FacilioChain;
import com.facilio.oldsandbox.command.AddNewOrgAndInstallForSandboxCommand;
import com.facilio.oldsandbox.command.AddSandboxCommand;
import com.facilio.oldsandbox.command.PackCurrentOrgForSandboxCommand;
import com.facilio.oldsandbox.command.ValidateAndFillDefaultValuesSandboxCommand;

public class BundleTransactionChainFactory {

	
	private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getCopyCustomizationChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchDefaultSystemBundleCommand());
        c.addCommand(new FetchBundleChangeSetCommand());
        c.addCommand(new PackBundleChangeSetCommand());
        return c;
    }
    
    public static FacilioChain addBundleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddBundleCommand());
        return c;
    }
    
    public static FacilioChain getBundleChangeSetChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchBundleCommand());
        c.addCommand(new FetchBundleChangeSetCommand());
        return c;
    }
    
    public static FacilioChain getCreateVersionChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchBundleCommand());
        c.addCommand(new FetchBundleChangeSetCommand());
        c.addCommand(new CreateBundleVersionCommand());
        c.addCommand(new FetchAllChangeSetForPackingCommand());
        c.addCommand(new PackBundleChangeSetCommand());
        return c;
    }
    
    public static FacilioChain getAllBundlesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchAllBundleCommand());
        return c;
    }
    
    public static FacilioChain getAllVersionsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchBundleCommand());
        c.addCommand(new FetchAllVersionCommand());
        return c;
    }
    
    public static FacilioChain getInstallBundleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ParseZIPToFolderContentCommand());
        c.addCommand(new AddInstalledBundleEntryCommand());
        c.addCommand(new InstallBundleComponentsCommand());
        return c;
    }
    
	public static FacilioChain getAllInstalledBundlesChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new GetAllInstalledBundlesCommand());
        return c;
	}

	public static FacilioChain getAddSandboxChain() {
		// TODO Auto-generated method stub
		FacilioChain c = getDefaultChain();
		
		c.addCommand(new ValidateAndFillDefaultValuesSandboxCommand());
		c.addCommand(new PackCurrentOrgForSandboxCommand());
		c.addCommand(new AddNewOrgAndInstallForSandboxCommand());			// will be connected with sandbox org.
		c.addCommand(new AddSandboxCommand());
		return c;
	}
	
	public static FacilioChain getUpdateSandboxChain() {
		// TODO Auto-generated method stub
		
		FacilioChain c = getDefaultChain();
		return c;
	}
	
	public static FacilioChain getSandboxChangeSetChain() {
		
		FacilioChain c = getDefaultChain();
		c.addCommand(new GetSandboxChangeSetCommand());
		return c;
	}

	public static FacilioChain getPullSandboxChangesChain() {

		FacilioChain c = getDefaultChain();
		c.addCommand(new PackSandboxChangeSetCommand());
		c.addCommand(new InstallSandboxChangeSetCommand());
		return c;
	}
}
