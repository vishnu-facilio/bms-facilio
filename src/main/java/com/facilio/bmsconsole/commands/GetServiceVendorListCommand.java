package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetServiceVendorListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ServiceContext service = (ServiceContext)context.get(FacilioConstants.ContextNames.RECORD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_VENDOR);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ServiceVendorContext> selectBuilder = new SelectRecordsBuilder<ServiceVendorContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ServiceVendorContext.class)
				.andCondition(CriteriaAPI.getCondition("SERVICE_ID", "serviceId", String.valueOf(service.getId()), NumberOperators.EQUALS))
				.fetchLookup((LookupField) fieldMap.get("vendor"))
				;
		List<ServiceVendorContext> serviceVendors = selectBuilder.get();
		service.setServiceVendors(serviceVendors);
	return false;
	}
	

}
