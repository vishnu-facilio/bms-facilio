package com.facilio.bmts.bmsconsole.commands;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EventContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AddEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get("event");
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> props = FieldUtil.getAsProperties(event);
			
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Event")
																.fields(FieldFactory.getEventFields())
																.addRecord(props);
			
			builder.save();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return false;
	}
}
