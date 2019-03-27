package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPurchaseRequestOrderRelation implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<PurchaseRequestContext> purchaseRequests = (List<PurchaseRequestContext>) context.get(FacilioConstants.ContextNames.PURCHASE_REQUESTS);
		FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		List<FacilioField> prfields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		PurchaseOrderContext purchaseOrder = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
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
	private void updatePurchaseRequestStatus (List<PurchaseRequestContext> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		for(PurchaseRequestContext pr : list) {
			pr.setStatus(Status.COMPLETED);
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(pr.getId(), module));
			updateBuilder.update(pr);
		}
	}
}
