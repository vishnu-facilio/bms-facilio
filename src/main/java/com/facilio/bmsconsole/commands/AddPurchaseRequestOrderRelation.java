package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddPurchaseRequestOrderRelation extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(context.get(FacilioConstants.ContextNames.PR_IDS) != null) {
			List<Long> purchaseRequestsIds = (List<Long>) context.get(FacilioConstants.ContextNames.PR_IDS);
			FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField statusField = modBean.getField("status", purchaseRequestModule.getName());
			FacilioField poField = modBean.getField("purchaseOrder", purchaseRequestModule.getName());
			updateMap.put("status", PurchaseRequestContext.Status.COMPLETED.ordinal()+1);
			updateMap.put("purchaseOrder", FieldUtil.getAsProperties(purchaseOrder));
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(poField);
			updatedfields.add(statusField);
			UpdateRecordBuilder<PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<PurchaseRequestContext>()
							.module(purchaseRequestModule)
							.fields(updatedfields)
							.andCondition(CriteriaAPI.getIdCondition(purchaseRequestsIds,purchaseRequestModule));
		     updateBuilder.updateViaMap(updateMap);
		}
		return false;
	}
	
	
	
}
