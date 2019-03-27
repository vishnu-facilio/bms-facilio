package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddPurchaseRequestOrderRelation implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<PurchaseRequestContext> purchaseRequests = (List<PurchaseRequestContext>) context.get(FacilioConstants.ContextNames.PURCHASE_REQUESTS);
		FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		List<FacilioField> prfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);

		  for(PurchaseRequestContext pr : purchaseRequests) {
					pr.setPurchaseOrder(purchaseOrder);
					pr.setStatus(PurchaseRequestContext.Status.COMPLETED);
					UpdateRecordBuilder<PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<PurchaseRequestContext>()
							.module(purchaseRequestModule)
							.fields(fields)
							.andCondition(CriteriaAPI.getIdCondition(pr.getId(),purchaseRequestModule));
					updateBuilder.update(pr);
		  }
		return false;
	}
	
	
	
}
