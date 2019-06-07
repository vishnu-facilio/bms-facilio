package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.HashMap;
import java.util.List;



public class ImportSpaceAction {
	

	private String spaceName;
	private Long spaceId;
	
	private String floorName;
	private Long floorId;
	
	private String buildingName;
	private Long buildingId;
	
	private String siteName;
	private Long siteId;
	
	private ImportBuildingAction parentBuilding = null;
    private HashMap<String, ImportBuildingAction> space = null;
	
	
    public Long addSpace(String spaceName, Long siteId , Long buildingId) throws Exception
	{
		SpaceContext space = new SpaceContext();
		
		space.setSpaceType(SpaceType.SPACE);
		space.setName(spaceName);
		space.setSiteId(siteId);
		if(buildingId != null) {
			space.setBuildingId(buildingId);
		}
		
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		
		InsertRecordBuilder<SpaceContext> builder = new InsertRecordBuilder<SpaceContext>()
				.moduleName(module.getName())
				.table(module.getTableName())
				.fields(modBean.getAllFields(module.getName()));
		
		long id = builder.insert(space);
		
		System.out.println("-------------> space : "+ spaceName+" Site ID "+ siteId);
		
		System.out.println("space created with the following Id :"+id);
	    //	building.setId(id);
		SpaceAPI.updateHelperFields(space);
		return id;
		
	}
    
    
    public Long addSpace(String spaceName, Long siteId, Long buildingId , Long floorId) throws Exception
	{
		SpaceContext space = new SpaceContext();
		
		space.setSpaceType(SpaceType.SPACE);
		space.setName(spaceName);
		space.setSiteId(siteId);
		space.setBuildingId(buildingId);
		space.setFloorId(floorId);
		
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		
		InsertRecordBuilder<SpaceContext> builder = new InsertRecordBuilder<SpaceContext>()
				.moduleName(module.getName())
				.table(module.getTableName())
				.fields(modBean.getAllFields(module.getName()));
		
		long id = builder.insert(space);
		
		System.out.println("-------------> space : "+ spaceName+" Site ID "+ siteId);
		
		System.out.println("Space created with the following Id :"+id);
	    //	building.setId(id);
		SpaceAPI.updateHelperFields(space);
		return id;
		
	}
	
	public Long getSpaceId(String spaceName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SpaceContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, spaceName, StringOperators.IS))
																	;
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}
	public Long getSpaceId(Long floorId,String spaceName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.SPACE);
		FacilioField field1 = modBean.getField("floor", FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SpaceContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, spaceName, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field1, ""+floorId, StringOperators.IS))
																	;
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}
	
	public Long getSpaceIdFromBuilding(Long buildingId,String spaceName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.SPACE);
		FacilioField field1 = modBean.getField("building", FacilioConstants.ContextNames.SPACE);
		FacilioField field2 = modBean.getField("floor", FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SpaceContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, spaceName, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field1, ""+buildingId, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field2, "", CommonOperators.IS_EMPTY))
																	;
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}
	public Long getSpaceIdFromSite(Long siteId,String spaceName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.SPACE);
		FacilioField field1 = modBean.getField("site", FacilioConstants.ContextNames.SPACE);
		FacilioField field2 = modBean.getField("building", FacilioConstants.ContextNames.SPACE);
		FacilioField field3 = modBean.getField("floor", FacilioConstants.ContextNames.SPACE);
		
		SelectRecordsBuilder<SpaceContext> selectBuilder = new SelectRecordsBuilder<SpaceContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SpaceContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, spaceName, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field1, ""+siteId, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field2, "", CommonOperators.IS_EMPTY))
																	.andCondition(CriteriaAPI.getCondition(field3, "", CommonOperators.IS_EMPTY))
																	;
		List<SpaceContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
		
	}

	public Long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}
	public Long getFloorId() {
		return floorId;
	}
	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}
	public Long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
}
