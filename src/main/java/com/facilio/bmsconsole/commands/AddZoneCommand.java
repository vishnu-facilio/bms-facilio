package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddZoneCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		List<Long> children = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(zone != null && children != null && !children.isEmpty()) 
		{
			zone.setSpaceType(SpaceType.ZONE);
			
			SpaceAPI.updateZoneInfo(zone, children);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		//	Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<ZoneContext> builder = new InsertRecordBuilder<ZoneContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
															
			long zoneId = builder.insert(zone);
			zone.setId(zoneId);
			SpaceAPI.updateHelperFields(zone);
			SpaceAPI.addZoneChildren(zone, children);
		}
		else 
		{
			throw new IllegalArgumentException("Zone Object cannot be null");
		}
		return false;
	}
	
//	private void addZoneChildren(ZoneContext zone, List<Long> childrenIds) throws SQLException, RuntimeException {
//		List<Map<String, Object>> childProps = new ArrayList<>();
//		for(long childId : childrenIds) {
//			Map<String, Object> prop = new HashMap<>();
//			prop.put("zoneId", zone.getId());
//			prop.put("basespaceId", childId);
//			childProps.add(prop);
//		}
//		
//		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
//														.table(ModuleFactory.getZoneRelModule().getTableName())
//														.fields(FieldFactory.getZoneRelFields())
//														.addRecords(childProps);
//		
//		insertBuilder.save();
//	}
//	
//	private void updateZoneInfo(ZoneContext zone, List<Long> childrenIds) throws Exception {
//		List<BaseSpaceContext> children = SpaceAPI.getBaseSpaces(childrenIds);
//		
//		long siteId = -1, buildingId = -1, floorId = -1;
//		boolean isFirst = true;
//		for(BaseSpaceContext child : children) {
//			if(isFirst) {
//				siteId = child.getSiteId();
//				isFirst = false;
//			}
//			else if(siteId != child.getSiteId()) {
//				siteId = -1;
//				break;
//			}
//		}
//		
//		isFirst = true;
//		for(BaseSpaceContext child : children) {
//			if(isFirst) {
//				buildingId = child.getBuildingId();
//				isFirst = false;
//			}
//			else if(siteId != child.getBuildingId()) {
//				buildingId = -1;
//				break;
//			}
//		}
//		
//		isFirst = true;
//		for(BaseSpaceContext child : children) {
//			if(isFirst) {
//				floorId = child.getFloorId();
//				isFirst = false;
//			}
//			else if(floorId != child.getFloorId()) {
//				floorId = -1;
//				break;
//			}
//		}
//		
//		if(siteId != -1) {
//			zone.setSiteId(siteId);
//		}
//		if(buildingId != -1) {
//			zone.setBuildingId(buildingId);
//		}
//		if(floorId != -1) {
//			zone.setFloorId(floorId);
//		}
//	}
}
