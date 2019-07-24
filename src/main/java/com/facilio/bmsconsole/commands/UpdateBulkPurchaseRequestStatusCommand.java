package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateBulkPurchaseRequestStatusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		List<Long> purchaseRequestsIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		int status = (Integer)context.get(FacilioConstants.ContextNames.STATUS);
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField statusField = modBean.getField("status", purchaseRequestModule.getName());
		updateMap.put("status", status);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		updatedfields.add(statusField);
		UpdateRecordBuilder<PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<PurchaseRequestContext>()
						.module(purchaseRequestModule)
						.fields(updatedfields)
						.andCondition(CriteriaAPI.getIdCondition(purchaseRequestsIds,purchaseRequestModule));
	     context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.updateViaMap(updateMap));
		return false;
	}

	
}
