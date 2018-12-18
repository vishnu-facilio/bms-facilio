package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.constants.FacilioConstants;

public class ControllerAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String deleteController() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, id);
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE, deleteReading);
		
		Chain deleteControllerSettings = TransactionChainFactory.getDeleteControllerChain();
		deleteControllerSettings.execute(context);
		
		setResult("result", "success");
		return SUCCESS; 
	}
	
	private Boolean deleteReading;
	public Boolean getDeleteReading() {
		if (deleteReading == null) {
			return false;
		}
		return deleteReading;
	}
	public void setDeleteReading(Boolean deleteReading) {
		this.deleteReading = deleteReading;
	}

}
