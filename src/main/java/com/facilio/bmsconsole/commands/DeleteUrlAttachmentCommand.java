package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class DeleteUrlAttachmentCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
				long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				FacilioModule module = ModuleFactory.getTemplateUrlAttachmentModule();
				List<FacilioField> fields = FieldFactory.getTemplateUrlFields();
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				  
				GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), ""+templateId, NumberOperators.EQUALS));

				context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
				
				return false;
	}

	

}
