package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ConnectionUtil {

	
	public static ConnectionContext getConnection(long connectionId) throws Exception {
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getIdCondition(connectionId, ModuleFactory.getConnectionModule()));

		List<Map<String, Object>> props = select.get();
		
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), ConnectionContext.class);
		}
		return null;
	}
	
	public static List<ConnectionContext> getAllConnections() throws Exception {
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.select(FieldFactory.getConnectionFields());

		List<Map<String, Object>> props = select.get();
		
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, ConnectionContext.class);
		}
		return null;
	}
}
