package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.PeopleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddClientUserCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ClientContext> clients = (List<ClientContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (clients != null && !clients.isEmpty()) {
			for (ClientContext client : clients) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
				List<FacilioField> fields = modBean.getAllFields(module.getName());
				
				FacilioModule ccModule = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
				List<FacilioField> ccFields = modBean.getAllFields(ccModule.getName());
			
				EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE,
						EventType.CREATE);

				if (eventType == EventType.CREATE) {
					ContactsContext primarycontact = addDefaultClientPrimaryContact(client);
					RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
				} else {
					if (StringUtils.isNoneEmpty(client.getPrimaryContactPhone())) {
						ContactsContext existingcontactForPhone = ContactsAPI
								.getContactforPhone(client.getPrimaryContactPhone(), client.getId(), false);
						if (existingcontactForPhone == null) {
							existingcontactForPhone = addDefaultClientPrimaryContact(client);
							RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module,
									fields);
						} else {
							existingcontactForPhone.setName(client.getPrimaryContactName());
							existingcontactForPhone.setEmail(client.getPrimaryContactEmail());
							RecordAPI.updateRecord(existingcontactForPhone, module, fields);
						}
					}
				}
					ClientContactContext cc = getDefaultClientContact(client);
					List<ClientContactContext> primarycontatsIfAny = PeopleAPI.getClientContacts(cc.getClient().getId(), true);
					ClientContactContext clientPrimaryContact = null;
					if(CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
						clientPrimaryContact = primarycontatsIfAny.get(0);
					}
					PeopleAPI.addParentPrimaryContactAsPeople(cc, ccModule, ccFields, cc.getClient().getId(), clientPrimaryContact);

			}
		}

		return false;
	}
	
	private ContactsContext addDefaultClientPrimaryContact(ClientContext client) throws Exception {
		ContactsAPI.unMarkPrimaryContact(-1, client.getId(), ContactType.CLIENT);
		ContactsContext contact = new ContactsContext();
		contact.setName(client.getPrimaryContactName() != null ? client.getPrimaryContactName() : client.getName());
		contact.setContactType(ContactType.CLIENT);
		contact.setClient(client);
		contact.setEmail(client.getPrimaryContactEmail());
		contact.setPhone(client.getPrimaryContactPhone());
		contact.setIsPrimaryContact(true);
		contact.setIsPortalAccessNeeded(false);
		return contact;
	}

	private ClientContactContext getDefaultClientContact(ClientContext client) throws Exception {
		ClientContactContext tc = new ClientContactContext();
		tc.setName(client.getPrimaryContactName());
		tc.setEmail(client.getPrimaryContactEmail());
		tc.setPhone(client.getPrimaryContactPhone());
		tc.setPeopleType(PeopleContext.PeopleType.CLIENT_CONTACT);
		tc.setClient(client);
		tc.setIsPrimaryContact(true);

		return tc;
	}
}
