package com.facilio.oldsandbox.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.oldsandbox.context.SandboxContext;

public class AddSandboxCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		SandboxContext sandbox = (SandboxContext) context.get(BundleConstants.Sandbox.SANDBOX);
		
		
		Map<String, Object> sandboxProps = FieldUtil.getAsProperties(sandbox);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSandboxModule().getTableName())
				.fields(FieldFactory.getSandboxFields())
				.addRecord(sandboxProps);
		
		insert.save();
		
		sandbox.setId((long)sandboxProps.get("id"));
		
		return false;
	}

}
