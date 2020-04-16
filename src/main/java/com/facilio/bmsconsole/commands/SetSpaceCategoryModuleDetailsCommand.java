package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class SetSpaceCategoryModuleDetailsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		SpaceContext space = (SpaceContext) context.get(FacilioConstants.ContextNames.SPACE);
		if(space != null) 
		{
			if(space.getSpaceCategory() != null) {
				SpaceCategoryContext spaceCategory = (SpaceCategoryContext) RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE_CATEGORY, space.getSpaceCategory().getId());
				if(spaceCategory != null && spaceCategory.getSpaceModuleId() > 0 && spaceCategory.getName().equals("Tenant Unit")) {
					SetTableNamesCommand.getForTenantUnitSpace().execute(context);
					TenantUnitSpaceContext ts = FieldUtil.cloneBean(space, TenantUnitSpaceContext.class);
					context.put(FacilioConstants.ContextNames.SPACE, ts);
					return false;
				}
			}
				SetTableNamesCommand.getForSpace().execute(context);
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null");
		}
		
	
		return false;
	}

}
