package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class DeleteShipmentRotatingAssetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ShipmentLineItemContext> lineItems = (List<ShipmentLineItemContext>)context.get(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		if(CollectionUtils.isNotEmpty(lineItems)) {
			for(ShipmentLineItemContext lineItem : lineItems) {
				markAssetFromStoreAsSysDeleted(context, lineItem.getAsset().getId());
			}
		}
		return false;
	}
	
	private void markAssetFromStoreAsSysDeleted(Context context, long assetId) throws Exception {
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(assetId));
		
		FacilioChain deleteAssetChain = FacilioChainFactory.getDeleteAssetChain();
		deleteAssetChain.execute(context);
	
	}

}
