package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateRequestedItemIssuedQuantityCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ItemTransactionsContext> itemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
	   if(CollectionUtils.isNotEmpty(itemTransactions) && itemTransactions.get(0).getRequestedLineItem() != null && itemTransactions.get(0).getRequestedLineItem().getId() > 0) {
		V3InventoryRequestLineItemContext lineItem = V3InventoryRequestAPI.getLineItem(itemTransactions.get(0).getRequestedLineItem().getId());
		   if(lineItem != null && itemTransactions.get(0).getTransactionStateEnum() == TransactionState.RETURN) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
				Map<String, Object> updateMap = new HashMap<>();
				FacilioField issuedQuantityField = modBean.getField("issuedQuantity", lineItemModule.getName());
				updateMap.put("issuedQuantity", lineItem.getQuantity() - itemTransactions.get(0).getQuantity());
				List<FacilioField> updatedfields = new ArrayList<FacilioField>();
				updatedfields.add(issuedQuantityField);
				UpdateRecordBuilder<InventoryRequestLineItemContext> updateBuilder = new UpdateRecordBuilder<InventoryRequestLineItemContext>()
								.module(lineItemModule)
								.fields(updatedfields)
								.andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), lineItemModule))
								;
			   updateBuilder.updateViaMap(updateMap);
		}
	}
		return false;

	}

}
