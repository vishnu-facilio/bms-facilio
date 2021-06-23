package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;

public class UpdateStateFlowDiagramCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		JSONObject stateFlowDiagram = (JSONObject) context.get(FacilioConstants.ContextNames.STATEFLOW_DIAGRAM);
		if (id > 0 && stateFlowDiagram != null) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getStateFlowModule().getTableName())
					.fields(Collections.singletonList(FieldFactory.getField("diagramJson", "DIAGRAM_JSON", ModuleFactory.getStateFlowModule(), FieldType.STRING)))
					.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getStateFlowModule()));
			Map<String, Object> map = new HashMap<>();
			map.put("diagramJson", stateFlowDiagram.toString());
			updateBuilder.update(map);
		}
		return false;
	}

}
