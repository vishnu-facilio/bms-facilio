package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddBuildingCommand extends FacilioCommand {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
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
			
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				builder.withChangeSet();
			}
															
			long id = builder.insert(building);
			building.setId(id);
			SpaceAPI.updateHelperFields(building);
			
			if (withChangeSet != null && withChangeSet) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, builder.getChangeSet());
			}
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
