package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertPRToPOCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			SelectRecordsBuilder<PurchaseRequestContext> builder = new SelectRecordsBuilder<PurchaseRequestContext>()
					.module(purchaseRequestModule)
					.beanClass(PurchaseRequestContext.class)
					.select(modBean.getAllFields(purchaseRequestModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, purchaseRequestModule));
			List<PurchaseRequestContext> list = builder.get();
			List<Long> prIds = new ArrayList<Long>();
			if (CollectionUtils.isNotEmpty(list)) {
				for (PurchaseRequestContext pr : list) {
					prIds.add(pr.getId());
				}
				FacilioModule prItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
				SelectRecordsBuilder<PurchaseRequestLineItemContext> b = new SelectRecordsBuilder<PurchaseRequestLineItemContext>()
						.module(prItemModule)
						.select(modBean.getAllFields(prItemModule.getName()))
						.beanClass(PurchaseRequestLineItemContext.class)
						.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", StringUtils.join(prIds, ","), NumberOperators.EQUALS));
				List<PurchaseRequestLineItemContext> lineItems = b.get();
				
				Map<Long, PurchaseRequestContext> requestsMap = FieldUtil.getAsMap(list);
				
				for (PurchaseRequestLineItemContext lineItem : lineItems) {
					PurchaseRequestContext purchaseRequest = requestsMap.get(lineItem.getPrid());
					purchaseRequest.addLineItem(lineItem);
				}
			}
			
			context.put(FacilioConstants.ContextNames.PURCHASE_REQUESTS, list);
			
			PurchaseOrderContext purchaseOrderContext = PurchaseOrderContext.fromPurchaseRequest(list);
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseOrderContext);
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
