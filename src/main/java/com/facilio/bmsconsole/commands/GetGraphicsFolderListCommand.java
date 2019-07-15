package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsFolderListCommand implements Command {


	@Override
	public boolean execute(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFolderFields())
				.table(ModuleFactory.getGraphicsFolderModule().getTableName()).limit(500);

		List<Map<String, Object>> props = selectBuilder.get();
		
		List<GraphicsFolderContext> graphicsFolderContexts = new ArrayList<>();
		
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				GraphicsFolderContext graphicsFolderContext = FieldUtil.getAsBeanFromMap(prop, GraphicsFolderContext.class);
				graphicsFolderContexts.add(graphicsFolderContext);
			}
		}
		
		context.put(FacilioConstants.ContextNames.GRAPHICS_FOLDERS, graphicsFolderContexts);
		
		return false;
	}
}
