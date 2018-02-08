package com.facilio.bmsconsole.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ReadingsAPI {
	
	public static void loadReadingParent(Collection<ReadingContext> readings) throws Exception {
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			
			try {
				SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																				.select(fields)
																				.module(module)
																				.beanClass(EnergyMeterContext.class);
				
				Map<Long, EnergyMeterContext> parents = selectBuilder.getAsMap();
				
				for(ReadingContext reading : readings) {
					Long parentId = reading.getParentId();
					if(parentId != null) {
						reading.setParent(parents.get(parentId));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

}
