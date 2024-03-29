package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.util.Strings;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class EditControllerSettingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		
		if (controller == null) {
			return false;
		}
		
		if (controller.getSiteId() <= 0) {
			throw new IllegalArgumentException("Site is mandatory.");
		}
		
		if (controller.getBuildingIds() != null && !controller.getBuildingIds().isEmpty()) {
			
			List<BuildingContext> buildings = SpaceAPI.getBuildingSpace(Strings.join(controller.getBuildingIds(), ','));
			if (buildings == null || buildings.isEmpty()) {
				throw new IllegalArgumentException("Building does not belong to site.");
			}
			
			for (BuildingContext building: buildings) {
				if (building.getSiteId() != controller.getSiteId()) {
					throw new IllegalArgumentException("Building does not belong to site.");
				}
			}
		}
		
		controller.setLastModifiedTime(System.currentTimeMillis());
		
		Map<String, Object> controllerProps = FieldUtil.getAsProperties(controller);
		
		List<FacilioField> fields = FieldFactory.getControllerFields();
		FacilioModule module = ModuleFactory.getControllerModule();
		int count = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(controller.getId(), module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.update(controllerProps);
		
		if (count == 0) {
			return false;
		}
		
		FacilioModule relModule = ModuleFactory.getControllerBuildingRelModule();
		new GenericDeleteRecordBuilder()
				.table(relModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(relModule))
				.andCondition(CriteriaAPI.getCondition("CONTROLLER_ID","controllerId", String.valueOf(controller.getId()),NumberOperators.EQUALS))
				.delete();
		
		ControllerAPI.addControllerBuildingRel(controller);
		
		return false;
	}

}
