package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetCampusCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long campusId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(campusId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(SiteContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", campusId)
					.fetchSupplement((LookupField) fieldMap.get("location"))
					.orderBy("ID");

			boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
			if (skipModuleCriteria) {
				builder.skipModuleCriteria();
			}

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
