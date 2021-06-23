package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetServiceVendorListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ServiceContext service = (ServiceContext)context.get(FacilioConstants.ContextNames.RECORD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_VENDOR);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ServiceVendorContext> selectBuilder = new SelectRecordsBuilder<ServiceVendorContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ServiceVendorContext.class)
				.andCondition(CriteriaAPI.getCondition("SERVICE_ID", "serviceId", String.valueOf(service.getId()), NumberOperators.EQUALS))
				.fetchSupplement((LookupField) fieldMap.get("vendor"))
				;
		List<ServiceVendorContext> serviceVendors = selectBuilder.get();
		service.setServiceVendors(serviceVendors);
	return false;
	}
	

}
