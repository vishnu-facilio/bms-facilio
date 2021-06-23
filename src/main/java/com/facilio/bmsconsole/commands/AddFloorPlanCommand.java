package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;


public class AddFloorPlanCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub		
		FloorPlanContext floorPlan = (FloorPlanContext) context.get(FacilioConstants.ContextNames.FLOOR_PLAN);
		
		 Long createdTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis());
	     Long modifiedTime = System.currentTimeMillis();
	     floorPlan.setSysCreatedTime(createdTime);
	     floorPlan.setSysModifiedTime(modifiedTime);
	     long userId = AccountUtil.getCurrentUser().getId();
//	     floorPlan.setModifiedBy(userId);
//	     floorPlan.setSysModifiedBy(AccountUtil.getCurrentUser());
//	     floorPlan.setCreatedBy(userId);
//	     floorPlan.setActive(floorPlan.isActive());
	     floorPlan.setFileId(floorPlan.getFileId());
	     
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFloorPlanModule().getTableName())
				.fields(FieldFactory.getFloorPlanFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(floorPlan);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		floorPlan.setId((Long) props.get("id"));
		floorPlan.setFloorPlanId((Long) props.get("id"));
		return false;
	}

}
