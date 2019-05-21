package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetIndependentSpaceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		Long categoryId = (Long) context.get(FacilioConstants.ContextNames.SPACE_CATEGORY);
		
		
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(SpaceContext.class)
				.select(fields)
				.orderBy("ID")
				.andCustomWhere("BaseSpace.SITE_ID = ? AND BaseSpace.BUILDING_ID IS NULL AND BaseSpace.FLOOR_ID IS NULL", siteId);
		
		if (categoryId != null && categoryId > 0) {
			builder.andCustomWhere("Space.SPACE_CATEGORY_ID = ?", categoryId);
		}

		List<SpaceContext> spaces = builder.get();
		context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
		
		return false;
	}

}
