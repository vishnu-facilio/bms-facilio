package com.facilio.events.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateEventResourcesMappingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		long resourceId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			ResourceContext resource = ResourceAPI.getResource(resourceId);
			if (resource != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BMS_EVENT);
				List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.BMS_EVENT);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

				BMSEventContext event = new BMSEventContext();
				event.setResource(resource);
				UpdateRecordBuilder<BMSEventContext> builder = new UpdateRecordBuilder<BMSEventContext>()
						.fields(Collections.singletonList(fieldMap.get("resource")))
						.module(module)
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("source"), source, StringOperators.IS));
				builder.update(event);
			}
		}
		else {
			EventContext event = new EventContext();
			event.setResourceId(resourceId);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.fields(EventConstants.EventFieldFactory.getEventFields())
					.table(EventConstants.EventModuleFactory.getEventModule().getTableName())
					.andCustomWhere("ORGID = ? AND SOURCE = ?", AccountUtil.getCurrentOrg().getOrgId(), source);
			updateBuilder.update(FieldUtil.getAsProperties(event));
		}
		return false;
	}

}
