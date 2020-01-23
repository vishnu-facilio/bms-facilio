package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetAllApplicationCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
	                .table(ModuleFactory.getApplicationModule().getTableName())
	                .select(FieldFactory.getApplicationFields());
		 
		List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(), ApplicationContext.class);
		context.put(FacilioConstants.ContextNames.APPLICATION, applications);
		return false;
	}

}
