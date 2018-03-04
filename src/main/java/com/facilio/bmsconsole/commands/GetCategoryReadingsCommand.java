package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetCategoryReadingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		
		if (categoryReadingRelModule == null) {
			return false;
		}
		
		long parentCategoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
		List<FacilioField> fields = FieldFactory.getCategoryReadingsFields(categoryReadingRelModule);
		
		if(parentCategoryId != -1) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(categoryReadingRelModule.getTableName())
															.andCustomWhere("PARENT_CATEGORY_ID = ?", parentCategoryId);
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			List<FacilioModule> readings = null;
			if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
				readings = getDefaultReadings();
			}
			else {
				readings = new ArrayList<>();
			}
			
			if(props != null && !props.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for(Map<String, Object> prop : props) {
					readings.add(modBean.getModule((long) prop.get("readingModuleId")));
				}
			}
			context.put(FacilioConstants.ContextNames.MODULE_LIST, readings);
		}
		else if(categoryReadingRelModule.getName().equals("spacecategoryreading")) {
			context.put(FacilioConstants.ContextNames.MODULE_LIST, getDefaultReadings());
		}
		
		return false;
	}
	
	private List<FacilioModule> getDefaultReadings() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> readings = new ArrayList<>();
		readings.add(modBean.getModule(FacilioConstants.ContextNames.CURRENT_OCCUPANCY_READING));
		readings.add(modBean.getModule(FacilioConstants.ContextNames.ASSIGNED_OCCUPANCY_READING));
		readings.add(modBean.getModule(FacilioConstants.ContextNames.TEMPERATURE_READING));
		readings.add(modBean.getModule(FacilioConstants.ContextNames.HUMIDITY_READING));
		readings.add(modBean.getModule(FacilioConstants.ContextNames.CO2_READING));
		readings.add(modBean.getModule(FacilioConstants.ContextNames.SET_POINT_READING));
		return readings;
	}

}
