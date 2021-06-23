package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedTermsContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AssociateTermsToContractCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<ContractAssociatedTermsContext> terms = (List<ContractAssociatedTermsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
		FacilioModule termsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_TERMS);
		ContractsAPI.updateTermsAssociated(recordId, terms);
			
		return false;
	}

}
