package com.facilio.bundle.action;

import java.io.File;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.Getter;

@Getter @Setter @Log4j
public class BundleAction extends V3Action{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	File bundleZip;
	String bundleZipName;
	String version;
	BundleContext bundle;
	
	public String copyCustomization() throws Exception {
		
		FacilioChain copyCustomizationChain = BundleTransactionChainFactory.getCopyCustomizationChain();
		
		FacilioContext context = copyCustomizationChain.getContext();
		
		copyCustomizationChain.execute();
		
		setData(BundleConstants.DOWNLOAD_URL, context.get(BundleConstants.DOWNLOAD_URL));
		
		return SUCCESS;
		
	}
	
	public String installBundle() throws Exception {
		
		FacilioChain installBundle = BundleTransactionChainFactory.getInstallBundleChain();
		
		FacilioContext context = installBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_ZIP_FILE, getBundleZip());
		context.put(BundleConstants.BUNDLE_ZIP_FILE_NAME, getBundleZipName());
		
		installBundle.execute();
		
		return SUCCESS;
		
	}
	
	public String addBundle() throws Exception {
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext context = addBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, getBundle());
		
		addBundle.execute();
		
		setData(BundleConstants.BUNDLE_CONTEXT,  getBundle());
		
		return SUCCESS;
	}

	public String getChangeSet() throws Exception {
		
		FacilioChain addBundle = BundleTransactionChainFactory.getBundleChangeSetChain();
		
		FacilioContext context = addBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, getBundle());
		
		addBundle.execute();
		
		setData(BundleConstants.BUNDLE_CHANGE_SET_LIST , context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST));
		
		return SUCCESS;
	}
	
	public String createVersion() throws Exception {
		
		FacilioChain addBundle = BundleTransactionChainFactory.getCreateVersionChain();
		
		FacilioContext context = addBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, getBundle());
		context.put(BundleConstants.VERSION, getVersion());
		
		addBundle.execute();
		
		setData(BundleConstants.BUNDLE_CONTEXT , context.get(BundleConstants.BUNDLE_CONTEXT));
		
		return SUCCESS;
	}
	
	public String getAllBundles() throws Exception {
		
		FacilioChain getallBundles = BundleTransactionChainFactory.getAllBundlesChain();
		
		FacilioContext context = getallBundles.getContext();
		
		getallBundles.execute();
		
		setData(BundleConstants.BUNDLE_CONTEXT_LIST , context.get(BundleConstants.BUNDLE_CONTEXT_LIST));
		
		return SUCCESS;
	}
	
	public String getAllVersions() throws Exception {
		
		FacilioChain getallVersions = BundleTransactionChainFactory.getAllVersionsChain();
		
		FacilioContext context = getallVersions.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, getBundle());
		
		getallVersions.execute();
		
		setData(BundleConstants.BUNDLE_VERSION_LIST , context.get(BundleConstants.BUNDLE_VERSION_LIST));
		
		return SUCCESS;
	}
}
