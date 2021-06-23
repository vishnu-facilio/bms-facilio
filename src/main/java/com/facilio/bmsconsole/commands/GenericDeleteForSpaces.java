package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class GenericDeleteForSpaces extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String modName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		if(modName.contentEquals("zone")) {
			checkIfZoneAssociatedToTenant(spaceId);
		}
		else {
			checkIfSpaceAssociatedToTenant(spaceId);
		}
		DeleteRecordBuilder<BaseSpaceContext> deleteAs = new DeleteRecordBuilder<BaseSpaceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(spaceId, module));
		deleteAs.markAsDelete();
		return false;
	}

	private void checkIfZoneAssociatedToTenant(Long zoneId) throws Exception {
		TenantsAPI.checkIfZoneOccupiedByTenant(zoneId);
		
	}
	private void checkIfSpaceAssociatedToTenant(Long spaceId) throws Exception {
		TenantsAPI.checkIfSpaceOccupiedByTenant(spaceId);
		
	}
	
	
}
