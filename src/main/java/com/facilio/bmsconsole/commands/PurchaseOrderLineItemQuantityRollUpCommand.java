package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

;

public class PurchaseOrderLineItemQuantityRollUpCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		Set<Long> lineItemIds = (Set<Long>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID);
		// TODO Auto-generated method stub
		if (lineItemIds != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule receiptsmodule = modBean.getModule(FacilioConstants.ContextNames.RECEIPTS);
			List<FacilioField> receiptsfields = modBean.getAllFields(FacilioConstants.ContextNames.RECEIPTS);
			Map<String, FacilioField> receiptsfieldsMap = FieldFactory.getAsMap(receiptsfields);
			
			FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			
			Set<Long> lineItemId = new HashSet<>();
			
			for (long id : lineItemIds) {
				double quantity = getTotalReceivedQuantity(id, receiptsmodule, receiptsfieldsMap);
				
				PurchaseOrderLineItemContext lineItem = new PurchaseOrderLineItemContext();
				lineItem.setId(id);
				lineItem.setQuantityReceived(quantity);
				UpdateRecordBuilder<PurchaseOrderLineItemContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderLineItemContext>()
						.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(id, pomodule));
				updateBuilder.update(lineItem);
				
				lineItemId.add(id);
			}
			
		}
	
		
		return false;
	}
	
	public static double getTotalReceivedQuantity(long id, FacilioModule module, Map<String, FacilioField> fieldsMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("received", "sum(case WHEN STATUS = 1 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		field.add(FieldFactory.getField("returns", "sum(case WHEN STATUS = 2 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));

		// SelectRecordsBuilder<ReceiptContext> builder = new
		// SelectRecordsBuilder<ReceiptContext>()
		// .select(field).moduleName(module.getName()).andCondition(CriteriaAPI
		// .getCondition(fieldsMap.get("receivableId"), String.valueOf(id),
		// NumberOperators.EQUALS))
		// .setAggregation();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.andCustomWhere(module.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.select(field).andCondition(CriteriaAPI.getCondition(fieldsMap.get("lineItem"), String.valueOf(id),
						NumberOperators.EQUALS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double received = 0, returns = 0;
			received = rs.get(0).get("received") != null ? (double) rs.get(0).get("received") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			
			return (received - returns);
		}
		return 0d;
	}

}
