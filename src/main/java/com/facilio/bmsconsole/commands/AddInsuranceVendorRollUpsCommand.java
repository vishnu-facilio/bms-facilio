package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.util.InsuranceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddInsuranceVendorRollUpsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VendorContext vendor = (VendorContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(vendor != null) {
			InsuranceContext ins = InsuranceAPI.getInsurancesForVendor(vendor.getId());
			if(ins != null && !vendor.getHasInsurance()) {
				vendor.setHasInsurance(true);
				updateVendorRollUp(vendor.getId());
			}
		}
		return false;
	}
	
	private void updateVendorRollUp(long vendorId) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
		
		
		UpdateRecordBuilder<VendorContext> updateBuilder = new UpdateRecordBuilder<VendorContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(String.valueOf(vendorId), module))
			;
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField hasInsurance = modBean.getField("hasInsurance", module.getName());
		updateMap.put("hasInsurance", true);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		updatedfields.add(hasInsurance);
	
		updateBuilder.updateViaMap(updateMap);
	
	}

}
