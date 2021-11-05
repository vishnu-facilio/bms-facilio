package com.facilio.bundle.command;

import com.facilio.chain.FacilioChain;

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
    
    public static FacilioChain getPopulateBundleChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PopulateBundleToNewOrgCommand());			//as per the old flow, not using now. keeping it just for reference.
        return c;
    }

	public static FacilioChain getAllInstalledBundlesChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new GetAllInstalledBundlesCommand());
        return c;
	}
}
