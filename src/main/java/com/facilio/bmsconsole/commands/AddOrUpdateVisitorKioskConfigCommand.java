package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;


public class AddOrUpdateVisitorKioskConfigCommand extends FacilioCommand {


	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		VisitorKioskContext visitorKioskContext = (VisitorKioskContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		
		
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
			
			builder.table(ModuleFactory.getVisitorKioskConfigModule().getTableName()).
			select(FieldFactory.getVisitorKioskConfigFields()).
			andCondition(CriteriaAPI.getIdCondition(visitorKioskContext.getId(), ModuleFactory.getVisitorKioskConfigModule()));
			
			Boolean isUpdate=false;
			
			List<Map<String, Object>> selectprops = builder.get();
			
			if (selectprops != null && selectprops.size() > 0) {
				isUpdate=true;
			}
			
					
			
			if (isUpdate) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getVisitorKioskConfigModule().getTableName())
						.fields(FieldFactory.getVisitorKioskConfigFields())
						.andCondition(CriteriaAPI.getIdCondition(visitorKioskContext.getId(), ModuleFactory.getVisitorKioskConfigModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(visitorKioskContext);
				int updatedRows = updateBuilder.update(props);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getVisitorKioskConfigModule().getTableName())
						.fields(FieldFactory.getVisitorKioskConfigFields());

				Map<String, Object> props = FieldUtil.getAsProperties(visitorKioskContext);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				long recordId = (Long) props.get("id");
				visitorKioskContext.setId(recordId);
			}
		
		
		return false;
	}

}

