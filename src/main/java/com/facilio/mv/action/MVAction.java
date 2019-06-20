	package com.facilio.mv.action;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class MVAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MVProjectWrapper mvProjectWrapper;
	
	public MVProjectWrapper getMvProject() {
		return mvProjectWrapper;
	}

	public void setMvProject(MVProjectWrapper mvProject) {
		this.mvProjectWrapper = mvProject;
	}
	
	public String addMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		Chain addMVProjectChain =  TransactionChainFactory.getAddMVProjectChain(); 
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String updateMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		context.put(MVUtil.MV_PROJECT_WRAPPER_OLD, MVUtil.getMVProject(mvProjectWrapper.getMvProject().getId()));
		
		Chain addMVProjectChain =  TransactionChainFactory.getUpdateMVProjectChain(); 
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String deleteMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		Chain deleteMVProjectChain =  TransactionChainFactory.getDeleteMVProjectChain(); 
		deleteMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
}
