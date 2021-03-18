package com.facilio.bundle.interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.chain.FacilioContext;

public interface BundleComponentInterface {

	JSONObject getFormatedObject(FacilioContext context) throws Exception;
	JSONArray getAllFormatedObject(FacilioContext context) throws Exception;
	
	void install(FacilioContext context) throws Exception;
}
