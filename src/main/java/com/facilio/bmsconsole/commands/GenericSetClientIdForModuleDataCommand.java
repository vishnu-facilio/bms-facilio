package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class GenericSetClientIdForModuleDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context
				.get(FacilioConstants.ContextNames.RECORD);
		if (record != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);

			List<FacilioField> fields = new ArrayList<>();
			FacilioField clientField = null;
			clientField = modBean.getField("client", module.getName());
			fields.add(clientField);
			
			long clientId = getClientIdForSite(record.getSiteId());
			record.setClientId(clientId);
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(record.getId(), module));
			updateBuilder.update(record);
		}
		return false;
	}

	private long getClientIdForSite(long siteId) throws Exception {
		SiteContext site = (SiteContext) RecordAPI.getRecord(FacilioConstants.ContextNames.SITE, siteId);
		if (site != null) {
			return site.getClient() != null ? site.getClient().getId() : -1;
		}
		return -1;
	}

}
