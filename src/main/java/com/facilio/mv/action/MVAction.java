	package com.facilio.mv.action;

	import com.facilio.bmsconsole.actions.FacilioAction;
	import com.facilio.bmsconsole.commands.TransactionChainFactory;
	import com.facilio.chain.FacilioContext;
	import com.facilio.mv.context.MVProjectWrapper;
	import com.facilio.mv.util.MVUtil;
	import org.apache.commons.chain.Chain;

public class MVAction extends FacilioAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	MVProjectWrapper mvProjectWrapper;
	
	Boolean isOpen;

	long mvProjectId = -1;
	
	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	public long getMvProjectId() {
		return mvProjectId;
	}

	public void setMvProjectId(long mvProjectId) {
		this.mvProjectId = mvProjectId;
	}

	public MVProjectWrapper getMvProjectWrapper() {
		return mvProjectWrapper;
	}

	public void setMvProjectWrapper(MVProjectWrapper mvProjectWrapper) {
		this.mvProjectWrapper = mvProjectWrapper;
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

	public String updateMVProjectMeta() throws Exception {

		FacilioContext context = new FacilioContext();

		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);

		Chain updateMVProjectChain =  TransactionChainFactory.getUpdateMVProjectMetaChain();
		updateMVProjectChain.execute(context);

		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String getMVProject() throws Exception {
		
		mvProjectWrapper = MVUtil.getMVProject(mvProjectId);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String getMVProjectList() throws Exception {
		
		setResult(MVUtil.MV_PROJECTS, MVUtil.getMVProjects(isOpen));
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
