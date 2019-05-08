package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetConnectedAppDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		String connectedAppLinkName = (String) context.get(FacilioConstants.ContextNames.LINK_NAME);
		
		if (connectedAppId > 0 || (connectedAppLinkName != null && !"".equals(connectedAppLinkName.trim()))) {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppFields())
					.table(ModuleFactory.getConnectedAppsModule().getTableName());
			
			if (connectedAppId > 0 ) {
				selectBuilder.andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
			}
			else {
//				selectBuilder.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getConnectedAppsModule()));
				selectBuilder.andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", connectedAppLinkName, StringOperators.IS));
			}
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				ConnectedAppContext connectedAppContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedAppContext.class);
				context.put(FacilioConstants.ContextNames.RECORD, connectedAppContext);
			}
		}
		return false;
	}

}
