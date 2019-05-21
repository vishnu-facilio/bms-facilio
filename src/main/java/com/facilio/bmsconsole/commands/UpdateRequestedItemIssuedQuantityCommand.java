package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateRequestedItemIssuedQuantityCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ItemTransactionsContext> itemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
	   if(CollectionUtils.isNotEmpty(itemTransactions) && itemTransactions.get(0).getRequestedLineItem() != null && itemTransactions.get(0).getRequestedLineItem().getId() > 0) {
		InventoryRequestLineItemContext lineItem = itemTransactions.get(0).getRequestedLineItem();
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
