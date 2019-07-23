package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetGraphicsDetailCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getGraphicsFields())
				.table(ModuleFactory.getGraphicsModule().getTableName());

		if (recordId > 0 ) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(recordId, ModuleFactory.getGraphicsModule()));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			GraphicsContext graphicsContext = FieldUtil.getAsBeanFromMap(props.get(0), GraphicsContext.class);
			context.put(FacilioConstants.ContextNames.GRAPHICS, graphicsContext);
		}
		
		return false;
	}
}
