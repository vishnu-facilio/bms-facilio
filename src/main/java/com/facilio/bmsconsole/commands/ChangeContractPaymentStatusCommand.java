package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ChangeContractPaymentStatusCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(recordId != null && recordId > 0) {
			ContractsContext contract = ContractsAPI.getContractDetails(recordId);
			if(contract != null) {
				Map<String, Object> updateMap = new HashMap<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule contractsModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACTS);
				FacilioField nextPaymentDateField = modBean.getField("nextPaymentDate", contractsModule.getName() );
				updateMap.put("nextPaymentDate", contract.computeNextPaymentDate());
				List<FacilioField> updatedfields = new ArrayList<FacilioField>();
				updatedfields.add(nextPaymentDateField);
				UpdateRecordBuilder<ContractsContext> updateBuilder = new UpdateRecordBuilder<ContractsContext>()
								.module(contractsModule)
								.fields(updatedfields)
								.andCondition(CriteriaAPI.getIdCondition(contract.getId(),contractsModule));
			     int rowsUpdated = updateBuilder.updateViaMap(updateMap);
			     context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
			}
		}
		
		return false;
	}

}
