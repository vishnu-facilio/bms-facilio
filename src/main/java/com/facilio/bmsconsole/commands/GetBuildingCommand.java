package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class GetBuildingCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long buildingId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(buildingId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(BuildingContext.class)
					.select(fields)
					.maxLevel(1)
					.andCustomWhere(module.getTableName()+".ID = ?", buildingId)
					.orderBy("ID");

			boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
			if (skipModuleCriteria) {
				builder.skipModuleCriteria();
			}
			
			List<BuildingContext> buildings = builder.get();	
			if(buildings.size() > 0) {
				BuildingContext building = buildings.get(0);
				LocationContext location=building.getLocation();
				if(location!=null)
				{
					location=SpaceAPI.getLocationSpace(building.getLocation().getId());
					building.setLocation(location);
				}
				if(building.getOperatingHour()!=-1){
					List<Long> businessHourIds=Collections.singletonList(building.getOperatingHour());
					List<BusinessHoursContext> businessHour = BusinessHoursAPI.getBusinessHours(businessHourIds);
					if(!businessHour.isEmpty()){
						building.setBusinessHour(businessHour.get(0));}
					}
				context.put(FacilioConstants.ContextNames.BUILDING, building);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Building ID : "+buildingId);
		}
		
		return false;
	}

}
