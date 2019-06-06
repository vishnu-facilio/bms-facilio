package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

;

public class FetchPurchaseOrderDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseOrderContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
			List<FacilioField> receivableFields = modBean.getAllFields(receivableModule.getName());
			
			SelectRecordsBuilder<ReceivableContext> recordsBuilder = new SelectRecordsBuilder<ReceivableContext>()
					.module(receivableModule)
					.beanClass(ReceivableContext.class)
					.select(receivableFields)
					.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS))
			        ;
		
			List<ReceivableContext> receivableList = recordsBuilder.get();
			if (receivableList.size() != 1) {
				throw new Exception("Receivable are invalid");
			}
			purchaseOrderContext.setReceivableContext(receivableList.get(0));
			
			PurchaseOrderAPI.setLineItems(purchaseOrderContext);
					
		}
		return false;
	}
	
	

}
