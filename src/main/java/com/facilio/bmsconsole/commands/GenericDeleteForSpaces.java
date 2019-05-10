package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GenericDeleteForSpaces implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
