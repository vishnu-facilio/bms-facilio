package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateConnectedAppSAMLCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectedAppSAMLContext connectedAppSAMLContext = (ConnectedAppSAMLContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (connectedAppSAMLContext != null) {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppSAMLFields())
					.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppSAMLContext.getConnectedAppId()), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
						.fields(FieldFactory.getConnectedAppSAMLFields())
						.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppSAMLContext.getConnectedAppId()), NumberOperators.EQUALS));
				
				Map<String, Object> props1 = FieldUtil.getAsProperties(connectedAppSAMLContext);
				updateBuilder.update(props1);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
						.fields(FieldFactory.getConnectedAppSAMLFields());

				Map<String, Object> props1 = FieldUtil.getAsProperties(connectedAppSAMLContext);

				insertBuilder.addRecord(props1);
				insertBuilder.save();
			}
		}
		
		return false;
	}

}
