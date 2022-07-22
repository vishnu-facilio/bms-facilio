package com.facilio.bundle.bean;

import java.io.File;
import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bundle.context.BundleChangeSetContext;

import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;

public interface BundleBean {
	
	public void populateSignupData(Account account, long orgId,JSONObject signupInfo) throws Exception; 

	public void installBundle(File bundleZipFile) throws Exception;
	
	public void createSandboxUnmanagedBundle(String sandboxName,String sandboxDomainName) throws Exception;

	public void createUsers(List<User> users,long sandboxOrgId,String sandboxDomain) throws Exception;
	
	public List<BundleChangeSetContext> getChangeSet() throws Exception;
	
	public File packSandboxChanges() throws Exception;
}
