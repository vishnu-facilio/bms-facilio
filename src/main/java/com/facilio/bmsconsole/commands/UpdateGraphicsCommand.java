package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateGraphicsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		GraphicsContext graphicsContext = (GraphicsContext) context.get(FacilioConstants.ContextNames.GRAPHICS);
		
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getGraphicsModule().getTableName());
			update.fields(FieldFactory.getGraphicsFields())
			.andCondition(CriteriaAPI.getIdCondition(graphicsContext.getId(), ModuleFactory.getGraphicsModule()));
			Map<String, Object> prop = FieldUtil.getAsProperties(graphicsContext);
			update.update(prop);
		
		return false;
	}
	

}
