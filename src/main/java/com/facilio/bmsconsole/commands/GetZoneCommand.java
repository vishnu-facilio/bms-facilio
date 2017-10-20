package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetZoneCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long zoneId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(zoneId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<ZoneContext> builder = new SelectRecordsBuilder<ZoneContext>()
					.connection(conn)
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(ZoneContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", zoneId)
					.orderBy("ID");

			List<ZoneContext> zones = builder.get();	
			if(zones.size() > 0) {
				ZoneContext zone = zones.get(0);
				context.put(FacilioConstants.ContextNames.ZONE, zone);
				
				context.put(GetNotesCommand.MODULEID_COLUMN, "ZONE_ID");
				context.put(GetNotesCommand.MODULE_ID, zoneId);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Zone ID : "+zoneId);
		}
		
		return false;
	}

}
