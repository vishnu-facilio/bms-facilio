package com.facilio.leed.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;

public class AddEnergyMeterCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		long leedId = (long) context.get(FacilioConstants.ContextNames.LEEDID);
		String meterName = (String)context.get(FacilioConstants.ContextNames.METERNAME);

		JSONObject meterInfo = new JSONObject();
		meterInfo.put("name", meterName);
		meterInfo.put("included", "true");
		meterInfo.put("native_unit", "kWh");
		meterInfo.put("type", "16"); //fuel_type 16 for { "id": 16, "type": "AZNM", "subtype": "WECC Southwest", "kind": "electricity", "resource": "Non-Renewable"}
		JSONObject meter_Id_Info = LeedIntegrator.createMeter(leedId, meterInfo);
		System.out.println(">>>>>>>>> meter_Id_Info : "+meter_Id_Info);
		JSONObject meterMessages = (JSONObject)meter_Id_Info.get("message");
		long meter_Id = (long)meterMessages.get("id");
		context.put(FacilioConstants.ContextNames.METERID, meter_Id);
		long fuel_type = (long)meterMessages.get("type");
		context.put(FacilioConstants.ContextNames.FUELTYPE, fuel_type);
		
		LeedAPI.addLeedEnergyMeter((FacilioContext)context);
		
		return false;
	}
	
	

}
