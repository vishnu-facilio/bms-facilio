package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardModule().getTableName())
					.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			dashboard.setId((Long) props.get("id"));
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		}
		
		return false;
	}

}