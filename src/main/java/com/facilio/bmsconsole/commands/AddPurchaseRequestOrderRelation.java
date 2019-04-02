package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddPurchaseRequestOrderRelation implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(context.get(FacilioConstants.ContextNames.PR_IDS) != null) {
			List<Long> purchaseRequestsIds = (List<Long>) context.get(FacilioConstants.ContextNames.PR_IDS);
			FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
	
			  for(Long prId : purchaseRequestsIds) {
				        PurchaseRequestContext pr = new PurchaseRequestContext();
						pr.setPurchaseOrder(purchaseOrder);
						pr.setStatus(PurchaseRequestContext.Status.COMPLETED);
						UpdateRecordBuilder<PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<PurchaseRequestContext>()
								.module(purchaseRequestModule)
								.fields(fields)
								.andCondition(CriteriaAPI.getIdCondition(prId,purchaseRequestModule));
						updateBuilder.update(pr);
			  }
		}
		return false;
	}
	
	
	
}
