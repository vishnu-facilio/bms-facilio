package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class PurchaseAssociatedAssetCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContractAssociatedAssetsContext> contractAssociatedAssets = (List<ContractAssociatedAssetsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		if(CollectionUtils.isNotEmpty(contractAssociatedAssets)) {
			StringJoiner ids = new StringJoiner(",");
			for(ContractAssociatedAssetsContext asset : contractAssociatedAssets) {
				ids.add(String.valueOf(asset.getId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField statusField = modBean.getField("status", module.getName());
			updateMap.put("status", ContractAssociatedAssetsContext.Status.PURCHASED.ordinal()+1);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(statusField);
			
			UpdateRecordBuilder<ContractAssociatedAssetsContext> updateBuilder = new UpdateRecordBuilder<ContractAssociatedAssetsContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(ids.toString(),module));
				    ;
			updateBuilder.updateViaMap(updateMap);
			//create po
			context.put(FacilioConstants.ContextNames.PURCHASE_ORDER, createPo(contractAssociatedAssets.get(0).getContractId(), contractAssociatedAssets));
		}
		return false;
	}
	
	private PurchaseOrderContext createPo (long contractId, List<ContractAssociatedAssetsContext> associatedAssets) throws Exception{
		ContractsContext contract = ContractsAPI.getContractDetails(contractId);
		PurchaseOrderContext purchaseOrderContext = new PurchaseOrderContext();
		purchaseOrderContext.setStatus(Status.REQUESTED);
		purchaseOrderContext.setVendor(contract.getVendor());
		purchaseOrderContext.setName("Payment for Purchasing leased Assets");
		purchaseOrderContext.setDescription("Payment for Purchasing leased Assets");
		purchaseOrderContext.setContractId(contractId);
		List<PurchaseOrderLineItemContext> lineItems = new ArrayList<PurchaseOrderLineItemContext>();
		for(ContractAssociatedAssetsContext asset : associatedAssets) {
			PurchaseOrderLineItemContext lineItem = new PurchaseOrderLineItemContext();
			lineItem.setInventoryType(InventoryType.OTHERS);
			lineItem.setOthersInfo("payment for asset " + asset.getAsset().getSerialNumber());
			lineItem.setQuantity(1);
			lineItem.setUnitPrice(asset.getLeaseEndValue());
			lineItems.add(lineItem);
		}
		purchaseOrderContext.setLineItems(lineItems);
		return purchaseOrderContext;
	}

}
