package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetFloorCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long floorId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(floorId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			
			SelectRecordsBuilder<FloorContext> builder = new SelectRecordsBuilder<FloorContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(FloorContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", floorId)
					.orderBy("ID");

			List<FloorContext> floors = builder.get();	
			if(floors.size() > 0) {
				FloorContext floor = floors.get(0);
				context.put(FacilioConstants.ContextNames.FLOOR, floor);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Floor ID : "+floorId);
		}
		
		return false;
	}

}
