package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
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
		
		if (controllerSettings.getBuildingId() > 0) {
			BuildingContext building = SpaceAPI.getBuildingSpace(controllerSettings.getBuildingId());
			if (building == null || building.getSiteId() != controllerSettings.getSiteId()) {
				throw new IllegalArgumentException("Building does not belong to site.");
			}
		}
		
		Map<String, Object> controllerSettingsprops = FieldUtil.getAsProperties(controllerSettings);
		
		List<FacilioField> fields = FieldFactory.getControllerFields();
		
		FacilioModule module = ModuleFactory.getControllerModule();
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(controllerSettings.getId(), module));
		
		long id = updateRecordBuilder.update(controllerSettingsprops);
		controllerSettings.setId(id);
		return false;
	}

}
