package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.util.Strings;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class EditControllerSettingsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ControllerContext controllerSettings = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		
		if (controllerSettings == null) {
			return false;
		}
		
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
		
		FacilioModule module = ModuleFactory.getControllerModule();
		
		long id = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(controllerSettings.getId(), module))
				.update(controllerSettingsprops);
		
		controllerSettings.setId(id);
		
		FacilioModule relModule = ModuleFactory.getControllerBuildingRelModule();
		new GenericDeleteRecordBuilder()
				.table(relModule.getTableName())
				.andCondition(CriteriaAPI.getCondition("CONTROLLER_ID","controllerId", Strings.join(controllerSettings.getBuildingIds(), ','),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(relModule))
				.delete();
		
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
		
		return false;
	}

}
