package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;

public class PurchaseAssociatedAssetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContractAssociatedAssetsContext> contractAssociatedAssets = (List<ContractAssociatedAssetsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		if(CollectionUtils.isNotEmpty(contractAssociatedAssets)) {
			//create po
			context.put(FacilioConstants.ContextNames.RECORD, createPo(contractAssociatedAssets.get(0).getContractId(), contractAssociatedAssets));
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
		purchaseOrderContext.setContract(contract);
		List<PurchaseOrderLineItemContext> lineItems = new ArrayList<PurchaseOrderLineItemContext>();
		for(ContractAssociatedAssetsContext asset : associatedAssets) {
			PurchaseOrderLineItemContext lineItem = new PurchaseOrderLineItemContext();
			lineItem.setInventoryType(InventoryType.OTHERS);
			lineItem.setRemarks("payment for asset " + AssetsAPI.getAssetInfo(asset.getAsset().getId()).getSerialNumber());
			lineItem.setQuantity(1);
			lineItem.setUnitPrice(asset.getLeaseEndValue());
			lineItems.add(lineItem);
		}
		purchaseOrderContext.setLineItems(lineItems);
		return purchaseOrderContext;
	}

}
