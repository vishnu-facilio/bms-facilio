	package com.facilio.mv.action;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.mv.context.MVProject;
import com.facilio.mv.util.MVUtil;

public class MVAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MVProject mvProject;
	
	public MVProject getMvProject() {
		return mvProject;
	}

	public void setMvProject(MVProject mvProject) {
		this.mvProject = mvProject;
	}
	
	public String addMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT, mvProject);
		
		Chain addMVProjectChain =  TransactionChainFactory.getAddMVProjectChain(); 
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT, mvProject);
		return SUCCESS;
	}
	
	public String updateMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT, mvProject);
		
		context.put(MVUtil.MV_PROJECT_OLD, MVUtil.getMVProject(mvProject.getId()));
		
		Chain addMVProjectChain =  TransactionChainFactory.getUpdateMVProjectChain(); 
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT, mvProject);
		return SUCCESS;
	}
	
	public String deleteMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT, mvProject);
		
		Chain deleteMVProjectChain =  TransactionChainFactory.getDeleteMVProjectChain(); 
		deleteMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT, mvProject);
		return SUCCESS;
	}
}
