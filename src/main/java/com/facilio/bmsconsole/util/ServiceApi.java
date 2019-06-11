package com.facilio.bmsconsole.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ServiceApi {


	public static ServiceContext getService(long serviceId) throws Exception {
		if (serviceId <= 0) {
			throw new IllegalArgumentException("No appropriate service found");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SERVICE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE);
		SelectRecordsBuilder<ServiceContext> selectBuilder = new SelectRecordsBuilder<ServiceContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ServiceContext.class)
				.andCondition(CriteriaAPI.getIdCondition(serviceId, module))
				;
		List<ServiceContext> services = selectBuilder.get();
		if(!CollectionUtils.isEmpty(services)) {
			return services.get(0);
		}
	 throw new IllegalArgumentException("No appropriate service found");
	}
}
