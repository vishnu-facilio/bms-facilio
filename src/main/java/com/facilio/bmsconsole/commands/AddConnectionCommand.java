package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddConnectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		connectionContext.setState(ConnectionContext.State.CREATED.getValue());
		
		fillDefaultfields(connectionContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.addRecord(FieldUtil.getAsProperties(connectionContext));
		
		insert.save();
		return false;
	}

	private void fillDefaultfields(ConnectionContext connectionContext) {

		connectionContext.setSysCreatedBy(AccountUtil.getCurrentUser());
		connectionContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());
	}

}
