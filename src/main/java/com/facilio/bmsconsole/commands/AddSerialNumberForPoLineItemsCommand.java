package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PoLineItemsSerialNumberContext;
import com.facilio.constants.FacilioConstants;

public class AddSerialNumberForPoLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<String> serialNumbers = (List<String>) context.get(FacilioConstants.ContextNames.SERIAL_NUMBERS);
		if(serialNumbers!=null) {
			PoLineItemsSerialNumberContext po = (PoLineItemsSerialNumberContext) context.get(FacilioConstants.ContextNames.RECORD);
			List<PoLineItemsSerialNumberContext> records = new ArrayList<>();
			for(String serialnumber : serialNumbers) {
				PoLineItemsSerialNumberContext record = new PoLineItemsSerialNumberContext();
				record.setLineItem(po.getLineItem());
				record.setPoId(po.getPoId());
				record.setReceiptId(po.getReceiptId());
				record.setSerialNumber(serialnumber);
				records.add(record);
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
		}
		return false;
	}

}
