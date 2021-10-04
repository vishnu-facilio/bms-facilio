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
	
	BundleContext bundle;
	
	public String copyCustomization() throws Exception {
		
		FacilioChain copyCustomizationChain = BundleTransactionChainFactory.getCopyCustomizationChain();
		
		FacilioContext context = copyCustomizationChain.getContext();
		
		copyCustomizationChain.execute();
		
		setData(BundleConstants.DOWNLOAD_URL, context.get(BundleConstants.DOWNLOAD_URL));
		
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
	
	public String installBundle() throws Exception {
		
		FacilioChain installBundle = BundleTransactionChainFactory.getInstallBundleChain();
		
		FacilioContext context = installBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_ZIP_FILE, getBundleZip());
		context.put(BundleConstants.BUNDLE_ZIP_FILE_NAME, getBundleZipName());
		
		installBundle.execute();
		
		return SUCCESS;
		
	}

}
