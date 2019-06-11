package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetServicePriceForVendorCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long serviceId = (Long)context.get(FacilioConstants.ContextNames.SERVICE);
		Long vendorId = (Long)context.get(FacilioConstants.ContextNames.VENDOR_ID);
		if(vendorId != null && vendorId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule serviceVendorModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
			List<FacilioField> selectFields = modBean.getAllFields(serviceVendorModule.getName());
			SelectRecordsBuilder<ServiceVendorContext> selectBuilder = new SelectRecordsBuilder<ServiceVendorContext>()
					.select(selectFields).table(serviceVendorModule.getTableName())
					.moduleName(serviceVendorModule.getName()).beanClass(ServiceVendorContext.class)
					.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("SERVICE_ID", "service", String.valueOf(serviceId), NumberOperators.EQUALS))
					;
			List<ServiceVendorContext> list = selectBuilder.get();
			if(!CollectionUtils.isEmpty(list)) {
				context.put(FacilioConstants.ContextNames.UNIT_PRICE, list.get(0).getLastPrice());
			}
		}
		else {
			throw new IllegalArgumentException("Vendor Id is not provided");
		}
		return false;
	
	}

}
