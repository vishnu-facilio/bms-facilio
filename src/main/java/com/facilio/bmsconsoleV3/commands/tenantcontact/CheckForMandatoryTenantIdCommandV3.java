package com.facilio.bmsconsoleV3.commands.tenantcontact;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class CheckForMandatoryTenantIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContactContext> tenantContacts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(tenantContacts)) {
            for(V3TenantContactContext tc : tenantContacts) {
                tc.setPeopleType(V3PeopleContext.PeopleType.TENANT_CONTACT.getIndex());
                if(tc.getTenant() == null || tc.getTenant().getId() <=0 ) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tenant Contact must have a tenant id associated");
                }
                //adding a default contact(old) when adding a new tenant contact for handling tenant portal till visitor host lookup is changed
                //should be removed
                if(StringUtils.isNotEmpty(tc.getEmail())) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
                    List<FacilioField> fields = modBean.getAllFields(module.getName());
                    V3ContactsContext contact = new V3ContactsContext();
                    contact.setName(tc.getName());
                    contact.setEmail(tc.getEmail());
                    contact.setPhone(tc.getPhone());
                    contact.setContactType(V3ContactsContext.ContactType.TENANT.getIndex());
                    contact.setTenant(tc.getTenant());
                    contact.setIsPrimaryContact(tc.isPrimaryContact());
                    V3RecordAPI.addRecord(true, java.util.Collections.singletonList(contact), module, fields);
                }
            }
        }
        return false;
    }
}
