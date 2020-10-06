package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.List;
import java.util.Map;

public class AddOrUpdateApplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ApplicationContext application = (ApplicationContext) context.get(FacilioConstants.ContextNames.APPLICATION);

		if (application != null) {
			application.setIsDefault(false);
			if (application.getId() > 0) {
				update(FieldUtil.getAsProperties(application), ModuleFactory.getApplicationModule(), FieldFactory.getApplicationFields(), application.getId());
			} else {
				add(FieldUtil.getAsProperties(application), ModuleFactory.getApplicationModule(), FieldFactory.getApplicationFields());
			}

		}
		return false;
	}

	private void add(Map<String, Object> map, FacilioModule module, List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields);
		builder.insert(map);
	}

	private void update(Map<String, Object> map, FacilioModule module, List<FacilioField> fields, long id) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields).andCondition(
						CriteriaAPI.getIdCondition(id, module));
		builder.update(map);
	}

}
