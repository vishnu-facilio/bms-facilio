package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class InsuranceAPI {

	
	public static InsuranceContext getInsurancesForVendor(long vendorId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INSURANCE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.INSURANCE);
		SelectRecordsBuilder<InsuranceContext> builder = new SelectRecordsBuilder<InsuranceContext>()
														.module(module)
														.beanClass(InsuranceContext.class)
														.select(fields)
														;
		
		if(vendorId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));
		}
		
		InsuranceContext record = builder.fetchFirst();
		return record;
	
	}
	
	public static void updateVendorRollUp(long vendorId) throws Exception{
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
