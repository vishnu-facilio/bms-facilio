package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetConnectedAppsListCommand implements Command{
	private static Logger log = LogManager.getLogger(GetConnectedAppsListCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppFields())
				.table(ModuleFactory.getConnectedAppsModule().getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getConnectedAppsModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<ConnectedAppContext> connectedApps = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				connectedApps.add(FieldUtil.getAsBeanFromMap(prop, ConnectedAppContext.class));
			}
			
			context.put(FacilioConstants.ContextNames.RECORD_LIST, connectedApps);
		}
		return false;
	}
}
