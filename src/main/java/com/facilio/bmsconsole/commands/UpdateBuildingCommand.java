package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UpdateBuildingCommand implements Command {

    private static Logger log = LogManager.getLogger(UpdateBuildingCommand.class.getName());
	
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING); 
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			try {
			UpdateRecordBuilder<BuildingContext> builder = new UpdateRecordBuilder<BuildingContext>()
					.moduleName(moduleName)
					.table(dataTableName)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(building.getId(),buildingModule));
			
															
			long id = builder.update(building);
			building.setId(id);
			SpaceAPI.updateHelperFields(building);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			
			}catch(Exception e)
			{
				log.info("Exception occurred ", e);
			}
		}
		else 
		{
			throw new IllegalArgumentException("Building Object cannot be null");
		}
		return false;
	}
}
