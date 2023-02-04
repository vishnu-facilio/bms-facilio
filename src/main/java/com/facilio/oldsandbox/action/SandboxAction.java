package com.facilio.oldsandbox.action;

import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.oldsandbox.context.SandboxContext;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter @Setter @Log4j
public class SandboxAction extends V3Action{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	SandboxContext sandbox;
	
	
	public String addSandbox() throws Exception {
		
		FacilioChain addSandbox = BundleTransactionChainFactory.getAddSandboxChain();
		
		FacilioContext context = addSandbox.getContext();
		
		context.put(BundleConstants.Sandbox.SANDBOX, sandbox);
		
		addSandbox.execute();
		
		setData(BundleConstants.Sandbox.SANDBOX, context.get(BundleConstants.Sandbox.SANDBOX));
		
		return SUCCESS;
		
	}
	
	public String updateSandbox() throws Exception {
		
		FacilioChain updateSandbox = BundleTransactionChainFactory.getUpdateSandboxChain();
		
		FacilioContext context = updateSandbox.getContext();
		
		context.put(BundleConstants.Sandbox.SANDBOX, sandbox);
		
		updateSandbox.execute();
		
		setData(BundleConstants.Sandbox.SANDBOX, context.get(BundleConstants.Sandbox.SANDBOX));
		
		return SUCCESS;
		
	}
	
	
	public String getSandboxChangeSet() throws Exception {
		
		FacilioChain getSandboxChangeSet = BundleTransactionChainFactory.getSandboxChangeSetChain();
		
		FacilioContext context = getSandboxChangeSet.getContext();
		
		context.put(BundleConstants.Sandbox.SANDBOX, sandbox);
		
		getSandboxChangeSet.execute();
		
		setData(BundleConstants.BUNDLE_CHANGE_SET_LIST , context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST));
		
		return SUCCESS;
		
	}
	
	public String pullSandboxChanges() throws Exception {
		
		FacilioChain getSandboxChangeSet = BundleTransactionChainFactory.getPullSandboxChangesChain();
		
		FacilioContext context = getSandboxChangeSet.getContext();
		
		context.put(BundleConstants.Sandbox.SANDBOX, sandbox);
		
		getSandboxChangeSet.execute();
		
		setData(BundleConstants.BUNDLE_CHANGE_SET_LIST , context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST));
		
		return SUCCESS;
		
	}
	
}
