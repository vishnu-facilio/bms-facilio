package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateWidgetFilterSettingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
//Update only the widget settings column
		List<Map<String,Object>> widgets=(List<Map<String,Object>>)context.get(FacilioConstants.ContextNames.WIDGET_UPDATE_LIST);
		
		
		if(widgets!=null&widgets.size()>0)
		{
			FacilioModule module=ModuleFactory.getWidgetModule();
			FacilioField field=FieldFactory.getAsMap(FieldFactory.getWidgetFields()).get("widgetSettingsJsonString");
			
			
			for(Map<String,Object> widget:widgets)
			{
				long widgetId=(long)widget.get("id");
				GenericUpdateRecordBuilder builder=new GenericUpdateRecordBuilder().table(module.getTableName()).fields(Collections.singletonList(field));
							
				builder.andCondition(CriteriaAPI.getIdCondition(widgetId, module));
				
				context.put(FacilioConstants.ContextNames.ROWS_UPDATED,builder.update(widget));
					
			}
			
			
		}
		return false;
	}

}
