package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetConnectedAppCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		String linkName = (String) context.get(FacilioConstants.ContextNames.LINK_NAME);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppFields())
				.table(ModuleFactory.getConnectedAppsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "true", BooleanOperators.IS));
		
		if (connectedAppId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
		}
		if (linkName != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", linkName, StringOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ConnectedAppContext connectedAppContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedAppContext.class);
			context.put(FacilioConstants.ContextNames.CONNECTED_APP, connectedAppContext);
		}
		
		return false;
	}
}