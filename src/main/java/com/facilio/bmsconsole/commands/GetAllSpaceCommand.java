package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetAllSpaceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.connection(conn)
				.dataTableName(dataTableName)
				.beanClass(SpaceContext.class)
				.select(fields)
				.orderBy("ID");

		List<SpaceContext> spaces = builder.getAsBean();
		context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
		
		return false;
	}

}
