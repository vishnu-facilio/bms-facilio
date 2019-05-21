package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

import java.util.List;

public class ImportSiteAction {
	
	private String siteName;
	private Long siteId;
	
	
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	public Long addSite(String siteName) throws Exception
	{
		
		// siteId = getSiteId(siteName);
		SiteContext site = new SiteContext();
			
				System.out.println("Inserting Site for the Query : "+siteName);
				site.setSpaceType(SpaceType.SITE);
				site.setName(siteName);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
				// String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				// String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
				// List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
				
				InsertRecordBuilder<SiteContext> builder = new InsertRecordBuilder<SiteContext>()
																.moduleName(module.getName())
																.table(module.getTableName())
																.fields(modBean.getAllFields(module.getName()));
				
				long id = builder.insert(site);
				System.out.println("Site created with the following Id :"+id);
				// site.setId(id);
				SpaceAPI.updateHelperFields(site);
				
	
		
		return id;
		
	}
	
	public Long getSiteId(String name) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
		FacilioField field = modBean.getField("name", FacilioConstants.ContextNames.SITE);
		SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
																	.select(fields)
																	.table(module.getTableName())
																	.moduleName(module.getName())
																	.maxLevel(0)
																	.beanClass(SiteContext.class)
																	.andCondition(CriteriaAPI.getCondition(field, name, StringOperators.IS))
																	;
		List<SiteContext> spaces = selectBuilder.get();
		
		if(spaces != null && !spaces.isEmpty()) {
			return spaces.get(0).getId();
		}
		return null;
	}
	
	
}
