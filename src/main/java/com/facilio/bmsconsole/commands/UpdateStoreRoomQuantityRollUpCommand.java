package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.constants.FacilioConstants;

public class UpdateStoreRoomQuantityRollUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(shipment.getStatusEnum() == ShipmentContext.Status.STAGED) {
		//	updateQuantityInFromStore();
		}
		else if(shipment.getStatusEnum() == ShipmentContext.Status.SHIPPED) {
		//	updateQuantityInToStore();
		}
		return false;
	}

	private void updateStoreRoomQuantity(long storeId, long itemType) {
		
	}
}
