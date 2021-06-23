package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class UpdateClientIdInSiteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ClientContext> clients = (List<ClientContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (clients != null && !clients.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			for (ClientContext client : clients) {
				if (client.getSiteIds() != null && !client.getSiteIds().isEmpty()) {
					for (long siteId : client.getSiteIds()) {
						SiteContext site = new SiteContext();
						site.setId(siteId);
						site.setClient(client);
						RecordAPI.updateRecord(site, module, fields);
					}
				}
			}
		}
		return false;
	}

}
