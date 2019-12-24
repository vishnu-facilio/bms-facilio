package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;


public class AddOrUpdateVisitorKioskConfigCommand extends FacilioCommand {



	@Override
	public boolean executeCommand(Context context) throws Exception {

		VisitorKioskContext visitorKioskContext = (VisitorKioskContext) context.get(FacilioConstants.ContextNames.RECORD);

		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();

		builder.table(module.getTableName()).
				select(fields).
				andCondition(CriteriaAPI.getIdCondition(visitorKioskContext.getId(), module));

		Boolean isUpdate=false;

		List<Map<String, Object>> selectprops = builder.get();

		if (selectprops != null && selectprops.size() > 0) {
			isUpdate=true;
		}

		if (isUpdate) {

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(visitorKioskContext.getId(), module));

			Map<String, Object> props = FieldUtil.getAsProperties(visitorKioskContext);
			updateBuilder.update(props);
		}
		else {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(visitorKioskContext);

			insertBuilder.addRecord(props);
			insertBuilder.save();
			long recordId = (Long) props.get("id");
			visitorKioskContext.setId(recordId);
		}


		return false;
	}

}

