package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AssociateTermsToPOCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<PoAssociatedTermsContext> terms = (List<PoAssociatedTermsContext>)context.get(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		PurchaseOrderAPI.updateTermsAssociated(recordId, terms);
			
		return false;
		
	}

}
