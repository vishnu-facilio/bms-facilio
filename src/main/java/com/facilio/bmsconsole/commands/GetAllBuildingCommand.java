package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetAllBuildingCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		
		//Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
				.table(module.getTableName())
				.moduleName(moduleName)
				.beanClass(BuildingContext.class)
				.select(fields)
				.orderBy(fieldMap.get("name").getColumnName());

		if (siteId != null && siteId > 0) {
			builder.andCustomWhere("BaseSpace.SITE_ID = ?", siteId);
		}
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}
		List<BuildingContext> buildings = builder.get();
		context.put(FacilioConstants.ContextNames.BUILDING_LIST, buildings);
		
		return false;
	}

}
