package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

import net.minidev.json.parser.JSONParser;


public class PortalInfoAction extends ActionSupport 
{		
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
		System.out.println(">>>>>>>>> portalInfoStr : "+portalInfoStr);
		System.out.println(">>>>>>>>> publicKeyFile : "+publicKeyFile);
		System.out.println(">>>>>>>>> publicKeyFileFileName : "+publicKeyFileFileName);
		System.out.println(">>>>>>>>> publicKeyFileContentType : "+publicKeyFileContentType);
		
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(portalInfoStr);
		setProtalInfo(FieldUtil.getAsBeanFromJson(json, PortalInfoContext.class));
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
		context.put(FacilioConstants.ContextNames.PORTALINFO, getProtalInfo());
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
		//portalInfoMap.put("signup_allowed",signup_allowed);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
		context.put(FacilioConstants.ContextNames.PORTALINFO, portalInfoMap);
		Chain updatePortalInfoChain = FacilioChainFactory.updatePortalInfoChain();
		updatePortalInfoChain.execute(context);
		
		return SUCCESS;
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
