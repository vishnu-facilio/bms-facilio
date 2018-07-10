package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateCampusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
//		SiteContext site = (SiteContext) context.get(FacilioConstants.ContextNames.SITE);
//		if(site != null) 
//		{
//			site.setSpaceType(SpaceType.SITE);
//			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
//			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
//			UpdateRecordBuilder<SiteContext> builder = new UpdateRecordBuilder<SiteContext>()
//					.moduleName(moduleName)
//					.table(dataTableName)
//					.fields(fields)
//					.andCustomWhere("SITE.ID = ?", site.getId());
//			long id = builder.update(site);
//			site.setId(id);
////			SpaceAPI.updateHelperFields(site);
//			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
//			context.put(FacilioConstants.ContextNames.PARENT_ID, id);
//			context.put(FacilioConstants.ContextNames.SITE, site);
//		}
//		else 
//		{
//			throw new IllegalArgumentException("Campus Object cannot be null");
//		}
		
		BaseSpaceContext building = (BaseSpaceContext) context.get(FacilioConstants.ContextNames.SITE);
		if(building != null) 
		{
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
			String moduleNameSpace = (String) context.get(FacilioConstants.ContextNames.SPACE_TYPE);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);	


			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			String dataTableName =  module.getTableName();

				
				UpdateRecordBuilder<BaseSpaceContext> builder = new UpdateRecordBuilder<BaseSpaceContext>()
						.moduleName(moduleName)
						.table(dataTableName)
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(Collections.singletonList(building.getId()) ,module));
															
			long id = builder.update(building);
			building.setId(id);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Building Object cannot be null");
		}
		return false;
	}

}
