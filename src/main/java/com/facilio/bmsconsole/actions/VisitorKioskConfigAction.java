package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.wms.constants.WmsEventType;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.util.WmsApi;

public class VisitorKioskConfigAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(VisitorKioskConfigAction.class.getName());
	private VisitorKioskContext visitorKioskConfig;
	
	public VisitorKioskContext getVisitorKioskConfig() {
		return visitorKioskConfig;
	}

	public void setVisitorKioskConfig(VisitorKioskContext visitorKioskConfig) {
		this.visitorKioskConfig = visitorKioskConfig;
	}

	public String addOrUpdate() throws Exception
	{
		//set ID from context obj to IDfield in parent context,SINCE genericbuilder and module does not support lookups
		if(visitorKioskConfig.getPrinter()!=null)
		{	
			visitorKioskConfig.setPrinterId(visitorKioskConfig.getPrinter().getId());
			
		}
		
		FacilioChain addOrUpdateChain = TransactionChainFactory.addOrUpdateVisitorKioskConfigChain();
		
    	FacilioContext addOrUpdateContext = addOrUpdateChain.getContext();
    	VisitorKioskContext visitorKioskContext=getVisitorKioskConfig();
    	addOrUpdateContext.put(FacilioConstants.ContextNames.RECORD, visitorKioskContext);

		addOrUpdateContext.put(FacilioConstants.ContextNames.MODULE, ModuleFactory.getVisitorKioskConfigModule());
		addOrUpdateContext.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getVisitorKioskConfigFields());

    	addOrUpdateChain.execute();	
    	try {
    	WmsApi.sendEventToDevice(visitorKioskContext.getId(), new WmsEvent().setEventType(WmsEventType.Device.RELOAD_CONF));
    	}
    	catch (Exception e) {
			log.info("error occured sending Event to device", e);
		}
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, addOrUpdateContext.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;
		
		
		
	}
	public String list() throws Exception{
		
		FacilioChain listChain=ReadOnlyChainFactory.getVisitorKioskListChain();
		listChain.execute();
		FacilioContext context=listChain.getContext();
		setResult(FacilioConstants.ContextNames.VISITOR_KIOSKS,context.get(ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	

}
