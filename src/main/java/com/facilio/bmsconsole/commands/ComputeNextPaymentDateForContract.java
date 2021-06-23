package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.ContractsContext.ContractType;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class ComputeNextPaymentDateForContract extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		String moduleName = null;
		if(recordId > 0) {
			ContractsContext contracts = ContractsAPI.getContractDetails(recordId);
			contracts.computeNextPaymentDate();
			if(contracts.getContractType() == ContractType.PURCHASE.getValue()) {
				moduleName = FacilioConstants.ContextNames.PURCHASE_CONTRACTS;
			}
			else if(contracts.getContractType() == ContractType.LABOUR.getValue()) {
				moduleName = FacilioConstants.ContextNames.LABOUR_CONTRACTS;
			}
			else if(contracts.getContractType() == ContractType.RENTAL_LEASE.getValue()) {
				moduleName = FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS;
			}
			else if(contracts.getContractType() == ContractType.WARRANTY.getValue()) {
				moduleName = FacilioConstants.ContextNames.WARRANTY_CONTRACTS;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			ContractsAPI.updateRecord(contracts, module, fields, true, (FacilioContext) context);
			
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.NEXT_PAYMENT_DATE, contracts.getNextPaymentDate());
			
					
		}
		return false;
	}

}
