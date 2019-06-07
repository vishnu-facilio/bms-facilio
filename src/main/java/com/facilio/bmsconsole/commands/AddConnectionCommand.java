package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddConnectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.addRecord(FieldUtil.getAsProperties(connectionContext));
		
		insert.save();
		return false;
	}

}
