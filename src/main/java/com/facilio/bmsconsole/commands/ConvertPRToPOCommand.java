package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

;

public class ConvertPRToPOCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (CollectionUtils.isNotEmpty(recordIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule purchaseRequestModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<V3PurchaseRequestContext> builder = new SelectRecordsBuilder<V3PurchaseRequestContext>()
					.module(purchaseRequestModule)
					.beanClass(V3PurchaseRequestContext.class)
					.select(modBean.getAllFields(purchaseRequestModule.getName()))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, purchaseRequestModule))
					;
					builder.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("vendor"),
					(LookupField) fieldsAsMap.get("storeRoom"),(LookupField) fieldsAsMap.get("shipToAddress"),(LookupField) fieldsAsMap.get("billToAddress"), (LookupField) fieldsAsMap.get("tax")))
					;
			List<V3PurchaseRequestContext> list = builder.get();
			List<Long> prIds = new ArrayList<Long>();
			if (CollectionUtils.isNotEmpty(list)) {
				for (V3PurchaseRequestContext pr : list) {
					prIds.add(pr.getId());
				}
				FacilioModule prItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
				SelectRecordsBuilder<V3PurchaseRequestLineItemContext> b = new SelectRecordsBuilder<V3PurchaseRequestLineItemContext>()
						.module(prItemModule)
						.select(modBean.getAllFields(prItemModule.getName()))
						.beanClass(V3PurchaseRequestLineItemContext.class)
						.andCondition(CriteriaAPI.getCondition("PR_ID", "purchaseRequest", StringUtils.join(prIds, ","), NumberOperators.EQUALS));
				List<V3PurchaseRequestLineItemContext> lineItems = b.get();
				
				Map<Long, V3PurchaseRequestContext> requestsMap = FieldUtil.getAsMap(list);
				
				for (V3PurchaseRequestLineItemContext lineItem : lineItems) {
					V3PurchaseRequestContext purchaseRequest = requestsMap.get(lineItem.getPurchaseRequest().getId());
					purchaseRequest.addLineItem(lineItem);
				}
			}
			
			context.put(FacilioConstants.ContextNames.PURCHASE_REQUESTS, list);

			V3PurchaseOrderContext purchaseOrderContext = V3PurchaseOrderContext.fromPurchaseRequest(list);
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseOrderContext);
		}
		return false;
	}

}
