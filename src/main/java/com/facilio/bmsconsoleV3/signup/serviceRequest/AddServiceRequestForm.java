package com.facilio.bmsconsoleV3.signup.serviceRequest;

import java.util.List;

import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;

public class AddServiceRequestForm extends SignUpData {

	
	public void addData() throws Exception {
		ApplicationContext appContext = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		
		FacilioChain addform = TransactionChainFactory.getAddFormCommand();
		FacilioContext context=addform.getContext();
		
		// -- DO NOT REMOVE THESE LINES  
		FacilioModule srattachments = Constants.getModBean().getModule("servicerequestsattachments");
		Constants.getModBean().getModule(srattachments.getModuleId());
		// -- DO NOT REMOVE THESE LINES
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceRequest");
		FacilioForm form = new FacilioForm();
		form.setAppId(appContext.getId());
		form.setAppLinkName(appContext.getLinkName());
		form.setDisplayName("SERVICE REQUEST");
		form.setId(-1);
		form.setName("default_serviceRequest_web");
		form.setStateFlowId(-99);
		context.put(FacilioConstants.ContextNames.FORM, form);
		
		addform.execute();
	}
}
