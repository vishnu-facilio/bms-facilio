package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateGraphicsFolderCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		GraphicsFolderContext graphicsFolderContext = (GraphicsFolderContext) context.get(FacilioConstants.ContextNames.GRAPHICS_FOLDER);
		
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getGraphicsFolderModule().getTableName());
			update.fields(FieldFactory.getGraphicsFolderFields())
			.andCondition(CriteriaAPI.getIdCondition(graphicsFolderContext.getId(), ModuleFactory.getGraphicsFolderModule()));
			Map<String, Object> prop = FieldUtil.getAsProperties(graphicsFolderContext);
			update.update(prop);
		
		return false;
	}
	

}
