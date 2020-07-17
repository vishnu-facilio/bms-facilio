package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddClientUserCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {		
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
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
						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
						List<FacilioField> fields = modBean.getAllFields(module.getName());
						
						FacilioModule ccModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
						List<FacilioField> ccFields = modBean.getAllFields(ccModule.getName());
					
						EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE,
								EventType.CREATE);

						if (eventType == EventType.CREATE) {
							V3ContactsContext primarycontact = addDefaultClientPrimaryContact(client);
							V3RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
						} else {
							if (StringUtils.isNoneEmpty(client.getPrimaryContactPhone())) {
								V3ContactsContext existingcontactForPhone = V3ContactsAPI
										.getContactforPhone(client.getPrimaryContactPhone(), client.getId(), false);
								if (existingcontactForPhone == null) {
									existingcontactForPhone = addDefaultClientPrimaryContact(client);
									V3RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module,
											fields);
								} else {
									existingcontactForPhone.setName(client.getPrimaryContactName());
									existingcontactForPhone.setEmail(client.getPrimaryContactEmail());
									V3RecordAPI.updateRecord(existingcontactForPhone, module, fields);
								}
							}
						}
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
	
	private V3ContactsContext addDefaultClientPrimaryContact(V3ClientContext client) throws Exception {
		V3ContactsAPI.unMarkPrimaryContact(-1, client.getId(), V3ContactsContext.ContactType.CLIENT);
		V3ContactsContext contact = new V3ContactsContext();
		contact.setName(client.getPrimaryContactName() != null ? client.getPrimaryContactName() : client.getName());
		contact.setContactType(V3ContactsContext.ContactType.CLIENT.getIndex());
		contact.setClient(client);
		contact.setEmail(client.getPrimaryContactEmail());
		contact.setPhone(client.getPrimaryContactPhone());
		contact.setIsPrimaryContact(true);
		contact.setIsPortalAccessNeeded(false);
		return contact;
	}

	private V3ClientContactContext getDefaultClientContact(V3ClientContext client) throws Exception {
		V3ClientContactContext tc = new V3ClientContactContext();
		tc.setName(client.getPrimaryContactName());
		tc.setEmail(client.getPrimaryContactEmail());
		tc.setPhone(client.getPrimaryContactPhone());
		tc.setPeopleType(V3PeopleContext.PeopleType.CLIENT_CONTACT.getIndex());
		tc.setClient(client);
		tc.setIsPrimaryContact(true);

		return tc;
	}
}

