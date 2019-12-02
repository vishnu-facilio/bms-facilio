package com.facilio.bmsconsole.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
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
	
}
