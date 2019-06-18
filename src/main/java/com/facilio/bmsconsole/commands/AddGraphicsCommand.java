package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddGraphicsCommand implements Command {

	@Override
public boolean execute(Context context) throws Exception {
		
		GraphicsContext graphicsContext = (GraphicsContext)context.get(FacilioConstants.ContextNames.GRAPHICS);
		
		
		graphicsContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(graphicsContext);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getGraphicsModule().getTableName())
				.fields(FieldFactory.getGraphicsFields())
				.addRecord(prop);
				
		insertRecordBuilder.save();
		
		long recorId = (Long) prop.get("id");
		graphicsContext.setId(recorId);
		
		return false;
	}

	
}
