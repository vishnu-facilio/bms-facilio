package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ImportBuildingAction {
	
	private ImportSiteAction parentSite = null;
	
	private String name;
	
	private String buildingName;
	private Long buildingId;
	
	private String siteName;
	private Long siteId;
	
	private HashMap<String, ImportSiteAction> building = null;
	
	public Long addBuilding(String buildingName, Long siteId) throws Exception
	{			
			BuildingContext building = new BuildingContext();
			building.setSpaceType(SpaceType.BUILDING);
			building.setName(buildingName);
			building.setSiteId(siteId);
			
		
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
			
			InsertRecordBuilder<BuildingContext> builder = new InsertRecordBuilder<BuildingContext>()
															.moduleName(module.getName())
															.table(module.getTableName())
															.fields(modBean.getAllFields(module.getName()));

															
			long id = builder.insert(building);
			
			System.out.println("-------------> building : "+ buildingName+" Site ID "+ siteId);
			
			System.out.println("building created with the following Id :"+id);
		    //	building.setId(id);
			SpaceAPI.updateHelperFields(building);
			// context.put(FacilioConstants.ContextNames.RECORD_ID, id);
				
		return id;
		
	}

	public Long getBuildingId(String buildingName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, buildingName, StringOperators.IS))
																	;
		List<BuildingContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
	}
	
	public Long getBuildingId(Long siteId,String buildingName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.BUILDING);
		FacilioField field1 = modBean.getField("site", FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> selectBuilder = new SelectRecordsBuilder<BuildingContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(BuildingContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, buildingName, StringOperators.IS))
																	.andCondition(CriteriaAPI.getCondition(field1, ""+siteId, NumberOperators.EQUALS))
																	;
		List<BuildingContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
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
