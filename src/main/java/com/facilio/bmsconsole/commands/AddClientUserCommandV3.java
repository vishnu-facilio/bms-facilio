package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddClientUserCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) 
		{
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) 
			{			
				for(Object record:records) 
				{
					if(record != null && (V3ClientContext)record != null)
					{
						V3ClientContext client = (V3ClientContext)record;
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioModule ccModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
						List<FacilioField> ccFields = modBean.getAllFields(ccModule.getName());

						if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
							V3ClientContactContext cc = getDefaultClientContact(client);
							List<V3ClientContactContext> primarycontatsIfAny = V3PeopleAPI.getClientContacts(cc.getClient().getId(), true);
							V3ClientContactContext clientPrimaryContact = null;
							if(CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
								clientPrimaryContact = primarycontatsIfAny.get(0);
							}
							V3PeopleAPI.addParentPrimaryContactAsPeople(cc, ccModule, ccFields, cc.getClient().getId(), clientPrimaryContact);
						}
					}
				}
			}
		}
		return false;
	}
	private V3ClientContactContext getDefaultClientContact(V3ClientContext client) throws Exception {
		V3ClientContactContext cc = new V3ClientContactContext();
		cc.setName(client.getPrimaryContactName());
		cc.setEmail(client.getPrimaryContactEmail());
		cc.setPhone(client.getPrimaryContactPhone());
		cc.setPeopleType(V3PeopleContext.PeopleType.CLIENT_CONTACT.getIndex());
		cc.setClient(client);
		cc.setIsPrimaryContact(true);

		return cc;
	}
}

