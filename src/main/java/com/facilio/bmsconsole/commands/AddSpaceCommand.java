package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class AddSpaceCommand extends FacilioCommand {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SpaceContext space = (SpaceContext) context.get(FacilioConstants.ContextNames.SPACE);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		if(space != null) 
		{
			space.setSpaceType(SpaceType.SPACE);
			List<SupplementRecord> supplements = new ArrayList<>();
			CommonCommandUtil.handleFormDataAndSupplement(fields, space.getData(), supplements);
			SpaceAPI.updateSiteAndBuildingId(space);
			if (space.getLocation() != null) {
	             TenantsAPI.addAddress(space.getName() + "_location" , space.getLocation());
	         }
			
			Map<Long, List<UpdateChangeSet>> changeSet = RecordAPI.addRecord(false, Collections.singletonList(space), module, fields, true, supplements);
			context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSet);
			SpaceAPI.updateHelperFields(space);
			context.put(FacilioConstants.ContextNames.RECORD_ID, space.getId());
			context.put(FacilioConstants.ContextNames.PARENT_ID, space.getId());
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null");
		}
		return false;
	}
	
}
