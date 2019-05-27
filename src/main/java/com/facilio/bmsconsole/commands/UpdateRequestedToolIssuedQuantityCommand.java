package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRequestedToolIssuedQuantityCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
	   if(CollectionUtils.isNotEmpty(toolTransactions) && toolTransactions.get(0).getRequestedLineItem() != null && toolTransactions.get(0).getRequestedLineItem().getId() > 0) {
		   InventoryRequestLineItemContext lineItem = InventoryRequestAPI.getLineItem(toolTransactions.get(0).getRequestedLineItem().getId());
			   if(lineItem != null && toolTransactions.get(0).getTransactionStateEnum() == TransactionState.RETURN) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
				Map<String, Object> updateMap = new HashMap<>();
				FacilioField issuedQuantityField = modBean.getField("issuedQuantity", lineItemModule.getName());
				updateMap.put("issuedQuantity", lineItem.getQuantity() - toolTransactions.get(0).getQuantity());
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
