package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.util.Strings;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
//import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
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
			
			if (controllerSettings.getBuildingIds() != null && !controllerSettings.getBuildingIds().isEmpty()) {
				
				List<BuildingContext> buildings = SpaceAPI.getBuildingSpace(Strings.join(controllerSettings.getBuildingIds(), ','));
				if (buildings == null || buildings.isEmpty()) {
					throw new IllegalArgumentException("Building does not belong to site.");
				}
				
				for (BuildingContext building: buildings) {
					if (building.getSiteId() != controllerSettings.getSiteId()) {
						throw new IllegalArgumentException("Building does not belong to site.");
					}
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
			
			if (controllerSettings.getBuildingIds() != null && !controllerSettings.getBuildingIds().isEmpty()) {
				GenericInsertRecordBuilder relBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getControllerBuildingRelModule().getTableName())
						.fields(FieldFactory.getControllerBuildingRelFields());
				for (long buildingId: controllerSettings.getBuildingIds()) {
					Map<String, Object> prop = new HashMap<>();
					prop.put("buildingId", buildingId);
					prop.put("controllerId", controllerSettings.getId());
					relBuilder.addRecord(prop);
				}
				relBuilder.save();
			}
		}
		return false;
	}
}
