package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.scriptengine.context.ScriptContext;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptModule;

@ScriptModule(moduleName = FacilioConstants.ContextNames.CONTACT)
public class FacilioContactModuleFunctions extends FacilioModuleFunctionImpl {

	@Override
	public void add(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		Object insertObject = objects.get(1);
		
		List<ContactsContext> contacts = new ArrayList<>();
		
		if(insertObject instanceof Map) {
			
			ContactsContext contact = FieldUtil.getAsBeanFromMap(((Map<String, Object>) insertObject), ContactsContext.class);
			contacts.add(contact);
		}
		else if (insertObject instanceof Collection) {
			List<Map<String,Object>> insertList = (List<Map<String,Object>>)insertObject;
			
			contacts = FieldUtil.getAsBeanListFromMapList(insertList, ContactsContext.class);
		}
		
		if(!CollectionUtils.isEmpty(contacts)) {
			FacilioChain c = TransactionChainFactory.addContactsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, contacts);
			c.execute();
			scriptContext.incrementTotalInsertCount();
		}
	}
}
