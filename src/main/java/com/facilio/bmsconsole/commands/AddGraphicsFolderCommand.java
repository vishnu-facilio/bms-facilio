package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddGraphicsFolderCommand extends FacilioCommand {

	@Override
public boolean executeCommand(Context context) throws Exception {
		
		GraphicsFolderContext graphicsFolderContext = (GraphicsFolderContext)context.get(FacilioConstants.ContextNames.GRAPHICS_FOLDER);
		
		
		graphicsFolderContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(graphicsFolderContext);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getGraphicsFolderModule().getTableName())
				.fields(FieldFactory.getGraphicsFolderFields())
				.addRecord(prop);
				
		insertRecordBuilder.save();
		
		long recorId = (Long) prop.get("id");
		graphicsFolderContext.setId(recorId);
		
		return false;
	}

	
}
