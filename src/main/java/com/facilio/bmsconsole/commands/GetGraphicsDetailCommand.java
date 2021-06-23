package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.GraphicsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetGraphicsDetailCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		boolean fetchOnlyMeta = (boolean) context.get(FacilioConstants.ContextNames.FETCH_ONLY_META);
		List<FacilioField> fieldsList = FieldFactory.getGraphicsFields();
		FacilioModule module = ModuleFactory.getGraphicsModule();
		if (fetchOnlyMeta == true) {
			fieldsList.remove(FieldFactory.getField("canvas", "CANVAS", module, FieldType.STRING));
			fieldsList.remove(FieldFactory.getField("variables", "VARIABLES", module, FieldType.STRING));
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fieldsList)
				.table(module.getTableName());

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
