package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PageAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	
	public String fetchPage() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ID, id);
		Chain chain = ReadOnlyChainFactory.getPageChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.PAGE, context.get(FacilioConstants.ContextNames.PAGE));
		
		return SUCCESS;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

}
