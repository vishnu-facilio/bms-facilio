package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

public class UpdateHelperFieldsCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		HashMap record = (HashMap)context.get("recordMap");
		
		List<V3TenantUnitSpaceContext> spaces = (List<V3TenantUnitSpaceContext>)record.get("tenantunit");
		
		if (spaces != null && !spaces.isEmpty()) {
			for (V3TenantUnitSpaceContext unitspace : spaces) {
				BaseSpaceContext updateSpace = new BaseSpaceContext();
				updateSpace.setSpaceId(unitspace.getId());
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = bean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
				UpdateRecordBuilder<BaseSpaceContext> updateBuilder = new UpdateRecordBuilder<BaseSpaceContext>()
																			.fields(bean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE))
																			.module(module)
																			.andCondition(CriteriaAPI.getIdCondition(unitspace.getId(), module))
																			;
				
				updateBuilder.update(updateSpace);
				context.put(FacilioConstants.ContextNames.SPACE,unitspace);
				context.put(FacilioConstants.ContextNames.PARENT_ID,unitspace.getId());
			}
		}
		
		return false;
	}

}
