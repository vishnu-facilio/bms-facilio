package com.facilio.bundle.interfaces;

import com.facilio.chain.FacilioContext;

public interface BundleComponentInterface {

	void getFormatedObject(FacilioContext context) throws Exception;
	
	void install(FacilioContext context) throws Exception;
	void postInstall(FacilioContext context) throws Exception;
	
	void getParentDetails(FacilioContext context) throws Exception;
	
	String getFileName(FacilioContext context) throws Exception;
}
