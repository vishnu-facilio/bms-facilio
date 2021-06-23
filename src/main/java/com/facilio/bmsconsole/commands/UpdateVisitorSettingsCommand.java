package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorSettingsContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateVisitorSettingsCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// Add Picklist entry for visitor and insert that ID Into table
		FacilioModule visitorSettingsModule=ModuleFactory.getVisitorSettingsModule();
		List<FacilioField> visitorSettingsFields=FieldFactory.getVisitorSettingsFields();
		VisitorSettingsContext visitorSettingsCtx=(VisitorSettingsContext)context.get(ContextNames.VISITOR_SETTINGS);
		
		long visitorSettingsId=visitorSettingsCtx.getVisitorTypeId();
		Map<String, Object> props = new HashMap<>();
		props=FieldUtil.getAsProperties(visitorSettingsCtx);
		
		GenericUpdateRecordBuilder updateBuilder=new GenericUpdateRecordBuilder();
		updateBuilder.table(visitorSettingsModule.getTableName()).
		fields(visitorSettingsFields).
		andCondition(CriteriaAPI.getCondition("VISITOR_TYPE_ID", "visitorType", visitorSettingsId+"", NumberOperators.EQUALS));
		updateBuilder.update(props);
					
		return false;
		
	}

}
