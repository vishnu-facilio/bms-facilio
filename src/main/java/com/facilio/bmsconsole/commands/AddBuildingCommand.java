package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddBuildingCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BuildingContext building = (BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING);
		if(building != null) 
		{
			building.setSpaceType(SpaceType.BUILDING);
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<BuildingContext> builder = new InsertRecordBuilder<BuildingContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
															
			long id = builder.insert(building);
			building.setId(id);
			SpaceAPI.updateHelperFields(building);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			context.put(FacilioConstants.ContextNames.PARENT_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Building Object cannot be null");
		}
		return false;
	}
}
