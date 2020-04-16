package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;

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
			RecordAPI.updateRecord(baseSpace, module, modBean.getAllFields(moduleName));
																		
			context.put(FacilioConstants.ContextNames.RECORD_ID, baseSpace.getId());
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null for updation");
		}
		return false;
	}

}
