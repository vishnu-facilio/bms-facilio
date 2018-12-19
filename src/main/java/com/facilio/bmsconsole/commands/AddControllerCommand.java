package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.aws.util.AwsUtil;
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
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddControllerCommand implements Command {
	@Override
	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		
		if(controller != null) {
			controller.setActive(true);
			controller.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			/*if (controllerSettings.getSiteId() <= 0) {
				throw new IllegalArgumentException("Site is mandatory.");
			}
			*/
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
			
			controller.setCreatedTime(System.currentTimeMillis());
			controller.setLastModifiedTime(controller.getCreatedTime());
			
			Map<String, Object> controllerProps = FieldUtil.getAsProperties(controller);
			
			List<FacilioField> fields = FieldFactory.getControllerFields();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table("Controller")
													.fields(fields)
													.addRecord(controllerProps);
			builder.save();
			controller.setId((long) controllerProps.get("id"));
			AwsUtil.addIotClient(AccountUtil.getCurrentOrg().getDomain(), controller.getMacAddr());
			
			ControllerAPI.addControllerBuildingRel(controller);
		}
		return false;
	}
}
