package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCampusCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SiteContext site = (SiteContext) context.get(FacilioConstants.ContextNames.SITE);
		if(site != null) 
		{
			site.setSpaceType(SpaceType.SITE);
			//Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			//Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<SiteContext> builder = new InsertRecordBuilder<SiteContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
			long id = builder.insert(site);
			site.setId(id);
			
			updateSiteId(id);
			
			SpaceAPI.updateHelperFields(site);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			context.put(FacilioConstants.ContextNames.PARENT_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Campus Object cannot be null");
		}
		return false;
	}

	private void updateSiteId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("resource");
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("siteId", id);
		
		new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(Arrays.asList(FieldFactory.getSiteIdField(module)))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.update(prop);
		
	}
}
