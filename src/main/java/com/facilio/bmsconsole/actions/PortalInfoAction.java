package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.struts2.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

import net.minidev.json.parser.JSONParser;


public class PortalInfoAction extends ActionSupport 
{		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getPortalInfo() throws Exception
	{
		FacilioContext context = new FacilioContext();
		Chain getPortalInfoChain = FacilioChainFactory.getPortalInfoChain();
		getPortalInfoChain.execute(context);
		setProtalInfo((PortalInfoContext)context.get(FacilioConstants.ContextNames.PORTALINFO));
		return SUCCESS;
	}
	public String saveSSOConfiguration() throws Exception 
	{
		
		
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(portalInfoStr);
		setPortalInfoMap(jsonToMap(json));
		//setProtalInfo(FieldUtil.getAsBeanFromJson(json, PortalInfoContext.class));
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
		//context.put(FacilioConstants.ContextNames.PORTALINFO, getProtalInfo());
		System.out.println("$$$$$$$$$$$$$ :"+portalInfoMap);
		context.put(FacilioConstants.ContextNames.PORTALINFO, portalInfoMap);
		context.put(FacilioConstants.ContextNames.PUBLICKEYFILE, publicKeyFile);
		context.put(FacilioConstants.ContextNames.PUBLICKEYFILETYPE, publicKeyFileContentType);
		context.put(FacilioConstants.ContextNames.PUBLICKEYFILENAME, publicKeyFileFileName);
		Chain updatePortalInfoChain = FacilioChainFactory.updatePortalSSOChain();
		updatePortalInfoChain.execute(context);
		
		return SUCCESS;
	}
	
	public String updateServicePortal() throws Exception 
	{
		System.out.println(">>>>>>>>> portalInfoMap : "+portalInfoMap);
		FacilioContext context = new FacilioContext();
		// portalInfoMap.put("signup_allowed",signup_allowed);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
		context.put(FacilioConstants.ContextNames.PORTALINFO, portalInfoMap);
		Chain updatePortalInfoChain = FacilioChainFactory.updatePortalInfoChain();
		updatePortalInfoChain.execute(context);
		
		return SUCCESS;
	}
	
	
	public static Map<String, Object> jsonToMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();

	    Iterator<String> keysItr = object.keySet().iterator();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        if(value instanceof JSONArray) {
	            value = jsonToList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = jsonToMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}

	public static List<Object> jsonToList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.size(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = jsonToList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = jsonToMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	
	private String portalInfoStr;
	
	public String getPortalInfoStr() {
		return portalInfoStr;
	}
	public void setPortalInfoStr(String portalInfoStr) {
		this.portalInfoStr = portalInfoStr;
	}

	private PortalInfoContext protalInfo;
	
	public PortalInfoContext getProtalInfo() {
		return protalInfo;
	}

	public void setProtalInfo(PortalInfoContext protalInfo) {
		this.protalInfo = protalInfo;
	}

	Map<String, Object> portalInfoMap;

	public void setPortalInfoMap(Map<String, Object> portalInfoMap) {
		this.portalInfoMap = portalInfoMap;
	}
	
	public Map<String, Object> getPortalInfoMap()
	{
		return this.portalInfoMap;
	}

	boolean signup_allowed;

	public boolean isSignup_allowed() {
		return signup_allowed;
	}

	public void setSignup_allowed(boolean signup_allowed) {
		this.signup_allowed = signup_allowed;
	}
	
	private File publicKeyFile;
	private String publicKeyFileFileName;
	private String publicKeyFileContentType;

	public File getPublicKeyFile() {
		return publicKeyFile;
	}
	public void setPublicKeyFile(File publicKeyFile) {
		this.publicKeyFile = publicKeyFile;
	}
	public String getPublicKeyFileFileName() {
		return publicKeyFileFileName;
	}
	public void setPublicKeyFileFileName(String publicKeyFileFileName) {
		this.publicKeyFileFileName = publicKeyFileFileName;
	}
	public String getPublicKeyFileContentType() {
		return publicKeyFileContentType;
	}
	public void setPublicKeyFileContentType(String publicKeyFileContentType) {
		this.publicKeyFileContentType = publicKeyFileContentType;
	}
	
	
}
