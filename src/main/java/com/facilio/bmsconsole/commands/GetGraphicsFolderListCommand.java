package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsFolderListCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFolderFields())
				.table(ModuleFactory.getGraphicsFolderModule().getTableName()).limit(500);

		List<Map<String, Object>> props = selectBuilder.get();
		
		List<GraphicsFolderContext> graphicsFolderContexts = new ArrayList<>();
		
		Boolean showChildrenGraphics = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_CHILDREN_GRAPHICS);
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				GraphicsFolderContext graphicsFolderContext = FieldUtil.getAsBeanFromMap(prop, GraphicsFolderContext.class);
				if (showChildrenGraphics != null && showChildrenGraphics) {
					graphicsFolderContext.setGraphics(getGraphicsListForGraphicsFolderId(graphicsFolderContext.getId()));
				}
				graphicsFolderContexts.add(graphicsFolderContext);
			}
		}
		
		context.put(FacilioConstants.ContextNames.GRAPHICS_FOLDERS, graphicsFolderContexts);
		
		return false;
	}
	
	public List<GraphicsContext> getGraphicsListForGraphicsFolderId(long graphicsFolderId) throws Exception {
			
			List<GraphicsContext> graphicsContexts = new ArrayList<>();
			if (graphicsFolderId > 0) {
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFields())
					.table(ModuleFactory.getGraphicsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("PARENT_FOLDER_ID", "parentFolderId", String.valueOf(graphicsFolderId), NumberOperators.EQUALS));
			
				List<Map<String, Object>> props = selectBuilder.get();
				if (props != null && !props.isEmpty()) {
					for(Map<String, Object> prop :props) {
						GraphicsContext graphicsContext = FieldUtil.getAsBeanFromMap(prop, GraphicsContext.class);
						graphicsContexts.add(graphicsContext);
					}
				}
			}
			
			return graphicsContexts;
			
		}
	
	
}
