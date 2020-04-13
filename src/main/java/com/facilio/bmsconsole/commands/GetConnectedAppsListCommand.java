package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetConnectedAppsListCommand extends FacilioCommand{
	private static Logger log = LogManager.getLogger(GetConnectedAppsListCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppFields())
				.table(ModuleFactory.getConnectedAppsModule().getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getConnectedAppsModule()));
				;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ConnectedAppContext> connectedApps = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				connectedApps.add(FieldUtil.getAsBeanFromMap(prop, ConnectedAppContext.class));
			}
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, connectedApps);
		}
		
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppSAMLFields())
				.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
				;
		props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ConnectedAppSAMLContext> connectedAppSAML = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				connectedAppSAML.add(FieldUtil.getAsBeanFromMap(prop, ConnectedAppSAMLContext.class));
			}
			
			context.put(FacilioConstants.ContextNames.CONNECTEDAPP_SAML_LIST, connectedAppSAML);
		}
		return false;
	}
}
