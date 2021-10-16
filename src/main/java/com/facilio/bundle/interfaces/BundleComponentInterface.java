package com.facilio.bundle.interfaces;

import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Condition;

public interface BundleComponentInterface {

	void getFormatedObject(FacilioContext context) throws Exception;
	void fillBundleXML(FacilioContext context) throws Exception;
	
	void install(FacilioContext context) throws Exception;
	void postInstall(FacilioContext context) throws Exception;
	
	void getParentDetails(FacilioContext context) throws Exception;
	
	void getAddedChangeSet(FacilioContext context) throws Exception;
	void getModifiedChangeSet(FacilioContext context) throws Exception;
	void getDeletedChangeSet(FacilioContext context) throws Exception;
	
	boolean isPackableComponent(FacilioContext context) throws Exception;
	Condition getFetchChangeSetCondition(FacilioContext context) throws Exception;
	
}
