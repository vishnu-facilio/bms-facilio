package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsFolderDetailCommand implements Command {


	@Override
	public boolean execute(Context context) throws Exception {
		
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFolderFields())
				.table(ModuleFactory.getGraphicsFolderModule().getTableName());

		if (recordId > 0 ) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(recordId, ModuleFactory.getGraphicsFolderModule()));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			GraphicsFolderContext graphicsFolderContext = FieldUtil.getAsBeanFromMap(props.get(0), GraphicsFolderContext.class);
			context.put(FacilioConstants.ContextNames.GRAPHICS_FOLDER, graphicsFolderContext);
		}
		
		return false;
	}
}
