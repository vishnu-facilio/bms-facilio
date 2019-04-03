package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateBulkPurchaseOrderStatusCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule purchaseOrderModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
		List<Long> purchaseOrdersIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		int status = (Integer)context.get(FacilioConstants.ContextNames.STATUS);
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField statusField = modBean.getField("status", purchaseOrderModule.getName());
		updateMap.put("status", status);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		updatedfields.add(statusField);
		UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
						.module(purchaseOrderModule)
						.fields(updatedfields)
						.andCondition(CriteriaAPI.getIdCondition(purchaseOrdersIds,purchaseOrderModule));
	     context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.updateViaMap(updateMap));
		return false;
	}
	

}
