package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.HashMap;
import java.util.List;

public class ImportFloorAction {
	private String name;
	private ImportBuildingAction parentBuilding = null;
    private HashMap<String, ImportBuildingAction> floor = null;
    
    private String spaceName = null;
	private Long spaceId = null;
	
	private String floorName = null;
	private Long floorId = null;
	
	private String buildingName = null;
	private Long buildingId = null;
	
	private String siteName = null;
	private Long siteId = null;
	
	public Long addFloor(String floorName, Long siteId , Long buildingId) throws Exception
	{
		FloorContext floor = new FloorContext();
		
		floor.setSpaceType(SpaceType.FLOOR);
		floor.setName(floorName);
		floor.setSiteId(siteId);
		floor.setBuildingId(buildingId);
		
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		
		InsertRecordBuilder<FloorContext> builder = new InsertRecordBuilder<FloorContext>()
				.moduleName(module.getName())
				.table(module.getTableName())
				.fields(modBean.getAllFields(module.getName()));
		
		long id = builder.insert(floor);
		
		System.out.println("-------------> floor : "+ floorName+" Site ID "+ siteId);
		
		System.out.println("floor created with the following Id :"+id);
	    //	building.setId(id);
		SpaceAPI.updateHelperFields(floor);
		return id;
		
	}
	
	public Long getFloorId(String floorName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.FLOOR);
		
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(FloorContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, floorName, StringOperators.IS))
																	;
		List<FloorContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}
	
	public Long getFloorId(Long buildingId,String floorName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.FLOOR);
		FacilioField field1 = modBean.getField("building", FacilioConstants.ContextNames.FLOOR);
		
		SelectRecordsBuilder<FloorContext> selectBuilder = new SelectRecordsBuilder<FloorContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(FloorContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, floorName, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field1, ""+buildingId, StringOperators.IS))
																	;
		List<FloorContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}
	
}
