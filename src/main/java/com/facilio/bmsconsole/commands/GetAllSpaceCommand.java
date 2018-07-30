package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetAllSpaceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		Long buildingId = (Long) context.get(FacilioConstants.ContextNames.BUILDING_ID);
		Long floorId = (Long) context.get(FacilioConstants.ContextNames.FLOOR_ID);
		Long categoryId = (Long) context.get(FacilioConstants.ContextNames.SPACE_CATEGORY);
		
		
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(SpaceContext.class)
				.select(fields)
				.orderBy("ID");
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		
		if (siteId != null && siteId > 0) {
			builder.andCustomWhere("BaseSpace.SITE_ID = ?", siteId);
		}
		else if (buildingId != null && buildingId > 0) {
			builder.andCustomWhere("BaseSpace.BUILDING_ID = ?", buildingId);
		}
		else if (floorId != null && floorId > 0) {
			builder.andCustomWhere("BaseSpace.FLOOR_ID = ?", floorId);
		}
		
		if (categoryId != null && categoryId > 0) {
			builder.andCustomWhere("Space.SPACE_CATEGORY_ID = ?", categoryId);
		}
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		List<SpaceContext> spaces = builder.get();
		context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
		
		return false;
	}

}
