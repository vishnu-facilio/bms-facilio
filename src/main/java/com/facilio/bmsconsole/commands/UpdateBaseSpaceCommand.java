package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import org.apache.commons.chain.Context;

import java.util.*;

public class UpdateBaseSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		BaseSpaceContext baseSpace = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.BASE_SPACE);
		if(baseSpace != null) 
		{

			String moduleName = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
			if(moduleName.equals("space")) {
				if(((SpaceContext)baseSpace).getSpaceCategory() != null) {
					SpaceCategoryContext spaceCategory = (SpaceCategoryContext) RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE_CATEGORY, ((SpaceContext)baseSpace).getSpaceCategory().getId());
					if(spaceCategory != null && spaceCategory.getSpaceModuleId() > 0 && spaceCategory.getName().equals("Tenant Unit")) {
						moduleName = FacilioConstants.ContextNames.TENANT_UNIT_SPACE;
					}
				}
			}
			context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);	


			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			CommonCommandUtil.handleFormDataAndSupplement(modBean.getAllFields(moduleName), baseSpace.getData(), Collections.EMPTY_LIST);
			Map<Long, List<UpdateChangeSet>> changeSet = RecordAPI.updateRecord(baseSpace, module, modBean.getAllFields(moduleName), true);

			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);

			if (changeSetMap == null) {
				changeSetMap = new HashMap<>();
			}
			context.put(ContextNames.CHANGE_SET_MAP, changeSetMap);

			CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, moduleName);

			baseSpace = (BaseSpaceContext) RecordAPI.getRecord(moduleName, baseSpace.getId());
			context.put(FacilioConstants.ContextNames.BASE_SPACE, baseSpace);
																		
			context.put(FacilioConstants.ContextNames.RECORD_ID, baseSpace.getId());
			List<Long> recordIds = new ArrayList<>();
			recordIds.add(baseSpace.getId());
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null for updation");
		}
		return false;
	}

}
