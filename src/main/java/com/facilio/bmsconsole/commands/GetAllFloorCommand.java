package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllFloorCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Long buildingId = (Long) context.get(FacilioConstants.ContextNames.BUILDING_ID);
		
	//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		SelectRecordsBuilder<FloorContext> builder = new SelectRecordsBuilder<FloorContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(FloorContext.class)
				.select(fields)
				.orderBy("-Floor.FLOOR_LEVEL desc, ID");
		
		if (buildingId != null && buildingId > 0) {
			builder.andCustomWhere("BaseSpace.BUILDING_ID = ?", buildingId);
		}
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		List<FloorContext> floors = builder.get();
		context.put(FacilioConstants.ContextNames.FLOOR_LIST, floors);
		
		return false;
	}

}
