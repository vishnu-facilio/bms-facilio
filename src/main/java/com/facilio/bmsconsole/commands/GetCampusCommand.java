package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetCampusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long campusId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(campusId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			
			SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(SiteContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", campusId)
					.orderBy("ID");

			List<SiteContext> campuses = builder.get();	
			if(campuses.size() > 0) {
				SiteContext campus = campuses.get(0);
				context.put(FacilioConstants.ContextNames.SITE, campus);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Campus ID : "+campusId);
		}
		
		return false;
	}

}
