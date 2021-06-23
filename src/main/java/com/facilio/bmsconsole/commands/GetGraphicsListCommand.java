package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsListCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFields())
				.table(ModuleFactory.getGraphicsModule().getTableName()).limit(500);

		List<Map<String, Object>> props = selectBuilder.get();
		
		List<GraphicsContext> graphicsContexts = new ArrayList<>();
		
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				GraphicsContext graphicsContext = FieldUtil.getAsBeanFromMap(prop, GraphicsContext.class);
				graphicsContexts.add(graphicsContext);
			}
		}
		
		context.put(FacilioConstants.ContextNames.GRAPHICS_LIST, graphicsContexts);
		
		return false;
	}
}
