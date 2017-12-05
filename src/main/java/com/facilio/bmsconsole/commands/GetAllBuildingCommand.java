package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAllBuildingCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		
		//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
				.table(module.getTableName())
				.moduleName(moduleName)
				.beanClass(BuildingContext.class)
				.select(fields)
				.orderBy(module.getTableName()+".ID");

		if (siteId != null && siteId > 0) {
			builder.andCustomWhere("BaseSpace.SITE_ID = ?", siteId);
		}
		
		List<BuildingContext> buildings = builder.get();
		context.put(FacilioConstants.ContextNames.BUILDING_LIST, buildings);
		
		return false;
	}

}
