package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddWidgetCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardWidgetContext widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
		if(widget != null) {			
			List<FacilioField> fields = FieldFactory.getWidgetFields();
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getWidgetModule().getTableName())
															.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(widget);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			widget.setId((Long) props.get("id"));
			context.put(FacilioConstants.ContextNames.WIDGET, widget);
		}
		
		return false;
	}

}
