package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
//import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddControllerCommand implements Command {
	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerContext controllerSettings = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		
		if(controllerSettings != null) {
			controllerSettings.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			if (controllerSettings.getSiteId() <= 0) {
				throw new IllegalArgumentException("Site is mandatory.");
			}
			
			if (controllerSettings.getBuildingId() > 0) {
				BuildingContext building = SpaceAPI.getBuildingSpace(controllerSettings.getBuildingId());
				if (building == null || building.getSiteId() != controllerSettings.getSiteId()) {
					throw new IllegalArgumentException("Building does not belong to site.");
				}
			}
			
			Map<String, Object> controllerSettingsprops = FieldUtil.getAsProperties(controllerSettings);
			
			List<FacilioField> fields = FieldFactory.getControllerFields();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table("Controller")
													.fields(fields)
													.addRecord(controllerSettingsprops);
			builder.save();
			controllerSettings.setId((long) controllerSettingsprops.get("id"));
		}
		return false;
	}
}
