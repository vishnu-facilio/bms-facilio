 package com.facilio.bmsconsole.commands;

 import com.facilio.accounts.util.AccountUtil;
 import com.facilio.bmsconsole.context.ControllerContext;
 import com.facilio.bmsconsole.modules.FieldFactory;
 import com.facilio.bmsconsole.modules.FieldUtil;
 import com.facilio.constants.FacilioConstants;
 import com.facilio.db.builder.GenericSelectRecordBuilder;
 import org.apache.commons.chain.Command;
 import org.apache.commons.chain.Context;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;

public class LoadControllerSettingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table("Controller")
				.select(FieldFactory.getControllerFields())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
		List<Map<String, Object>> settings = builder.get();
		
		if(settings != null && !settings.isEmpty()) {
			List<ControllerContext> controllercontext = new ArrayList<>();
			for(Map<String, Object> rule : settings) {
				ControllerContext controlcontext = FieldUtil.getAsBeanFromMap(rule, ControllerContext.class);
				controllercontext.add(controlcontext);
			}
			context.put(FacilioConstants.ContextNames.CONTROLLER_SETTINGS, controllercontext);
		}
		
		return false;
	}

}
