package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

public class UpdateBuildingCommand extends FacilioCommand {

    private static Logger log = LogManager.getLogger(UpdateBuildingCommand.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BaseSpaceContext building = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.BUILDING);
		if(building != null) 
		{
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);				
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
				
				UpdateRecordBuilder<BaseSpaceContext> builder = new UpdateRecordBuilder<BaseSpaceContext>()
						.moduleName(moduleName)
						.table(module.getTableName())
						.fields(module.getFields())
						.andCondition(CriteriaAPI.getIdCondition(Collections.singletonList(building.getId()) ,module));
															
			long id = builder.update(building);
			building.setId(id);
//			SpaceAPI.updateHelperFields(building);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Building Object cannot be null");
		}
		return false;
	}
}
