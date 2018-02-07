package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

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
			SpaceAPI.updateHelperFields(site);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Campus Object cannot be null");
		}
		return false;
	}
}
