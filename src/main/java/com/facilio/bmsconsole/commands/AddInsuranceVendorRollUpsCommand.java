package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.*;
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
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

public class AddInsuranceVendorRollUpsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VendorContext vendor = (VendorContext)context.get(FacilioConstants.ContextNames.RECORD);
		Map<String, List<? extends ModuleBaseWithCustomFields>> data = (Map<String, List<? extends ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		if(vendor != null) {
			InsuranceContext ins = InsuranceAPI.getInsurancesForVendor(vendor.getId());
			if(ins != null && !vendor.getHasInsurance()) {
				if (MapUtils.isNotEmpty(data)) {
					List<VendorContext> vendors = (List<VendorContext>) data.get(FacilioConstants.ContextNames.VENDORS);
					if (CollectionUtils.isNotEmpty(vendors)) {
						vendors.get(0).setHasInsurance(true);
					}
				}

				vendor.setHasInsurance(true);
				InsuranceAPI.updateVendorRollUp(vendor.getId());
			}
		}
		return false;
	}
	
}
