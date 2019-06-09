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
	
	public String addMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT, mvProject);
		
		Chain addMVProjectChain =  TransactionChainFactory.getAddMVProjectChain(); 
		addMVProjectChain.execute(context);
		
		setResult("mvProject", mvProject);
		return SUCCESS;
	}
}
