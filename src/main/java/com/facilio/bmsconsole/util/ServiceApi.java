package com.facilio.bmsconsole.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

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
